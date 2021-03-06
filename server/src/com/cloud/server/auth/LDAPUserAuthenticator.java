/**
 *  Copyright (C) 2011 Citrix.com, Inc.  All rights reserved.
 * 
 * This software is licensed under the GNU General Public License v3 or later.
 * 
 * It is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 */

package com.cloud.server.auth;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import javax.ejb.Local;
import javax.naming.AuthenticationException;
import javax.naming.ConfigurationException;
import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

import org.apache.log4j.Logger;

import com.cloud.api.ApiConstants.LDAPParams;
import com.cloud.configuration.Config;
import com.cloud.configuration.dao.ConfigurationDao;
import com.cloud.server.ManagementServer;
import com.cloud.user.UserAccount;
import com.cloud.user.dao.UserAccountDao;
import com.cloud.utils.component.ComponentLocator;


@Local(value={UserAuthenticator.class})
public class LDAPUserAuthenticator extends DefaultUserAuthenticator {
    public static final Logger s_logger = Logger.getLogger(LDAPUserAuthenticator.class);

    private ConfigurationDao _configDao;
    private UserAccountDao _userAccountDao;
    
    @Override
    public boolean authenticate(String username, String password, Long domainId, Map<String, Object[]> requestParameters ) {
        if (s_logger.isDebugEnabled()) {
            s_logger.debug("Retrieving user: " + username);
        }
        UserAccount user = _userAccountDao.getUserAccount(username, domainId);
        if (user == null) {
            s_logger.debug("Unable to find user with " + username + " in domain " + domainId);
            return false;
        }

        String url = _configDao.getValue(LDAPParams.hostname.toString());
        if (url==null){
            s_logger.debug("LDAP authenticator is not configured.");
            return false;
        }
        String port = _configDao.getValue(LDAPParams.port.toString());
        String queryFilter = _configDao.getValue(LDAPParams.queryfilter.toString());
        String searchBase = _configDao.getValue(LDAPParams.searchbase.toString());
        String useSSL = _configDao.getValue(LDAPParams.usessl.toString());
        String bindDN = _configDao.getValue(LDAPParams.dn.toString());
        String bindPasswd = _configDao.getValue(LDAPParams.passwd.toString());
        
        try {
            // get all params
            Hashtable<String, String> env = new Hashtable<String, String>(11);
            env.put(Context.INITIAL_CONTEXT_FACTORY,"com.sun.jndi.ldap.LdapCtxFactory");
            String protocol = "ldap://" ;
            if (new Boolean(useSSL)){
            	env.put(Context.SECURITY_PROTOCOL, "ssl");
                protocol="ldaps://" ;
            }
            env.put(Context.PROVIDER_URL, protocol + url  + ":" + port);

            if (bindDN != null && bindPasswd != null){
                env.put(Context.SECURITY_PRINCIPAL, bindDN);
                env.put(Context.SECURITY_CREDENTIALS, bindPasswd);
            }
            else {
            	// Use anonymous authentication
            	env.put(Context.SECURITY_AUTHENTICATION, "none");
            }
           // Create the initial context
            DirContext ctx = new InitialDirContext(env);
            // use this context to search

            // substitute the queryFilter with this user info
            queryFilter = queryFilter.replaceAll("\\%u", username);
            queryFilter = queryFilter.replaceAll("\\%n", user.getFirstname() + " " + user.getLastname());
            queryFilter = queryFilter.replaceAll("\\%e", user.getEmail());
            

            SearchControls sc = new SearchControls();
            String[] searchFilter = { "dn" };
            sc.setReturningAttributes(new String[0]); //return no attributes
            sc.setReturningAttributes(searchFilter);
            sc.setSearchScope(SearchControls.SUBTREE_SCOPE);
            sc.setCountLimit(1);
            
            // Search for objects with those matching attributes
            NamingEnumeration<SearchResult> answer = ctx.search(searchBase, queryFilter,  sc);
            SearchResult sr = (SearchResult)answer.next();
            String cn = sr.getName();
            answer.close();
            ctx.close();
            
            s_logger.info("DN from LDAP =" + cn);
            
            // check the password
            env = new Hashtable<String, String>(11);
            env.put(Context.INITIAL_CONTEXT_FACTORY,"com.sun.jndi.ldap.LdapCtxFactory");
            protocol = "ldap://" ;
            if (new Boolean(useSSL)){
            	env.put(Context.SECURITY_PROTOCOL, "ssl");
                protocol="ldaps://" ;
            }
            env.put(Context.PROVIDER_URL, protocol + url  + ":" + port);
            env.put(Context.SECURITY_PRINCIPAL, cn + "," + searchBase);
            env.put(Context.SECURITY_CREDENTIALS, password);
            // Create the initial context
            ctx = new InitialDirContext(env);
            ctx.close();
            
        } catch (NamingException ne) {
            ne.printStackTrace();
            s_logger.warn("Authentication failed due to " + ne.getMessage());
            return false;
        }
        catch (Exception e){
        	e.printStackTrace();
            s_logger.warn("Unknown error encountered " + e.getMessage());
            return false;
        }
        
        // authenticate
        return true;
    }

    public boolean configure(String name, Map<String, Object> params)
            throws ConfigurationException {
        super.configure(name, params);
        ComponentLocator locator = ComponentLocator.getLocator(ManagementServer.Name);
        _configDao = locator.getDao(ConfigurationDao.class);
        _userAccountDao = locator.getDao(UserAccountDao.class);
        return true;
    }
}
