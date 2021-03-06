/**
 *  Copyright (C) 2010 Cloud.com, Inc.  All rights reserved.
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

package com.cloud.alert;

import java.util.Map;

import javax.ejb.Local;
import javax.naming.ConfigurationException;

import org.apache.log4j.Logger;

import com.cloud.alert.AlertAdapter;
import com.cloud.alert.AlertManager;
import com.cloud.consoleproxy.ConsoleProxyManager;
import com.cloud.dc.DataCenterVO;
import com.cloud.dc.dao.DataCenterDao;
import com.cloud.storage.secondary.SecStorageVmAlertEventArgs;
import com.cloud.storage.secondary.SecondaryStorageVmManager;
import com.cloud.utils.component.Inject;
import com.cloud.utils.events.SubscriptionMgr;
import com.cloud.vm.SecondaryStorageVmVO;
import com.cloud.vm.dao.SecondaryStorageVmDao;

@Local(value=AlertAdapter.class)
public class SecondaryStorageVmAlertAdapter implements AlertAdapter {
	
	private static final Logger s_logger = Logger.getLogger(SecondaryStorageVmAlertAdapter.class);
    private String _name;
    
	@Inject private AlertManager _alertMgr;
	@Inject private DataCenterDao _dcDao;
	@Inject private SecondaryStorageVmDao _ssvmDao;
    
    public void onSSVMAlert(Object sender, SecStorageVmAlertEventArgs args) {
    	if(s_logger.isDebugEnabled())
    		s_logger.debug("received secondary storage vm alert");
    	
    	DataCenterVO dc = _dcDao.findById(args.getZoneId());
    	 SecondaryStorageVmVO secStorageVm = args.getSecStorageVm();
    	if(secStorageVm == null)
    		secStorageVm = _ssvmDao.findById(args.getSecStorageVmId());
    	
    	switch(args.getType()) {
    	case SecStorageVmAlertEventArgs.SSVM_CREATED :
        	if(s_logger.isDebugEnabled())
        		s_logger.debug("New secondary storage vm created, zone: " + dc.getName() + ", secStorageVm: " + 
        			secStorageVm.getHostName() + ", public IP: " + secStorageVm.getPublicIpAddress() + ", private IP: " + 
        			secStorageVm.getPrivateIpAddress());
			break;
    		
    	case SecStorageVmAlertEventArgs.SSVM_UP :
        	if(s_logger.isDebugEnabled())
        		s_logger.debug("Secondary Storage Vm is up, zone: " + dc.getName() + ", secStorageVm: " + 
        			secStorageVm.getHostName() + ", public IP: " + secStorageVm.getPublicIpAddress() + ", private IP: " + 
        			secStorageVm.getPrivateIpAddress());
    		
			_alertMgr.sendAlert(
				AlertManager.ALERT_TYPE_SSVM,
				args.getZoneId(),
				secStorageVm.getPodIdToDeployIn(),
				"Secondary Storage Vm up in zone: " + dc.getName() + ", secStorageVm: " + secStorageVm.getHostName() + ", public IP: " + secStorageVm.getPublicIpAddress() 
				 	+ ", private IP: " + (secStorageVm.getPrivateIpAddress() == null ? "N/A" : secStorageVm.getPrivateIpAddress()),
			 	"Secondary Storage Vm up (zone " + dc.getName() + ")" 	
			);
			break;
    		
    	case SecStorageVmAlertEventArgs.SSVM_DOWN :
        	if(s_logger.isDebugEnabled())
        		s_logger.debug("Secondary Storage Vm is down, zone: " + dc.getName() + ", secStorageVm: " + 
        			secStorageVm.getHostName() + ", public IP: " + secStorageVm.getPublicIpAddress() + ", private IP: " + 
        			(secStorageVm.getPrivateIpAddress() == null ? "N/A" : secStorageVm.getPrivateIpAddress()));
    		
			_alertMgr.sendAlert(
				AlertManager.ALERT_TYPE_SSVM,
				args.getZoneId(),
				secStorageVm.getPodIdToDeployIn(),
				"Secondary Storage Vm down in zone: " + dc.getName() + ", secStorageVm: " + secStorageVm.getHostName() + ", public IP: " + secStorageVm.getPublicIpAddress() 
				 	+ ", private IP: " + (secStorageVm.getPrivateIpAddress() == null ? "N/A" : secStorageVm.getPrivateIpAddress()),
			 	"Secondary Storage Vm down (zone " + dc.getName() + ")" 	
			);
			break;
    		
    	case SecStorageVmAlertEventArgs.SSVM_REBOOTED :
        	if(s_logger.isDebugEnabled())
        		s_logger.debug("Secondary Storage Vm is rebooted, zone: " + dc.getName() + ", secStorageVm: " + 
        			secStorageVm.getHostName() + ", public IP: " + secStorageVm.getPublicIpAddress() + ", private IP: " + 
        			(secStorageVm.getPrivateIpAddress() == null ? "N/A" : secStorageVm.getPrivateIpAddress()));
    		
			_alertMgr.sendAlert(
				AlertManager.ALERT_TYPE_SSVM,
				args.getZoneId(),
				secStorageVm.getPodIdToDeployIn(),
				"Secondary Storage Vm rebooted in zone: " + dc.getName() + ", secStorageVm: " + secStorageVm.getHostName() + ", public IP: " + secStorageVm.getPublicIpAddress() 
				 	+ ", private IP: " + (secStorageVm.getPrivateIpAddress() == null ? "N/A" : secStorageVm.getPrivateIpAddress()),
			 	"Secondary Storage Vm rebooted (zone " + dc.getName() + ")" 	
			);
			break;
			
    	case SecStorageVmAlertEventArgs.SSVM_CREATE_FAILURE :
        	if(s_logger.isDebugEnabled())
        		s_logger.debug("Secondary Storage Vm creation failure, zone: " + dc.getName() + ", secStorageVm: " + 
        			secStorageVm.getHostName() + ", public IP: " + secStorageVm.getPublicIpAddress() + ", private IP: " + 
        			(secStorageVm.getPrivateIpAddress() == null ? "N/A" : secStorageVm.getPrivateIpAddress()));
    		
			_alertMgr.sendAlert(
				AlertManager.ALERT_TYPE_SSVM,
				args.getZoneId(),
				secStorageVm.getPodIdToDeployIn(),
				"Secondary Storage Vm creation failure. zone: " + dc.getName() + ", secStorageVm: " + secStorageVm.getHostName() + ", public IP: " + secStorageVm.getPublicIpAddress() 
				 	+ ", private IP: " + (secStorageVm.getPrivateIpAddress() == null ? "N/A" : secStorageVm.getPrivateIpAddress()) 
				 	+ ", error details: " + args.getMessage(),
			 	"Secondary Storage Vm creation failure (zone " + dc.getName() + ")"
			);
    		break;
    		
    	case SecStorageVmAlertEventArgs.SSVM_START_FAILURE :
        	if(s_logger.isDebugEnabled())
        		s_logger.debug("Secondary Storage Vm startup failure, zone: " + dc.getName() + ", secStorageVm: " + 
        			secStorageVm.getHostName() + ", public IP: " + secStorageVm.getPublicIpAddress() + ", private IP: " + 
        			(secStorageVm.getPrivateIpAddress() == null ? "N/A" : secStorageVm.getPrivateIpAddress()));
    		
			_alertMgr.sendAlert(
				AlertManager.ALERT_TYPE_SSVM,
				args.getZoneId(),
				secStorageVm.getPodIdToDeployIn(),
				"Secondary Storage Vm startup failure. zone: " + dc.getName() + ", secStorageVm: " + secStorageVm.getHostName() + ", public IP: " + secStorageVm.getPublicIpAddress() 
				 	+ ", private IP: " + (secStorageVm.getPrivateIpAddress() == null ? "N/A" : secStorageVm.getPrivateIpAddress()) 
				 	+ ", error details: " + args.getMessage(),
			 	"Secondary Storage Vm startup failure (zone " + dc.getName() + ")" 	
			);
    		break;
    	
    	case SecStorageVmAlertEventArgs.SSVM_FIREWALL_ALERT :
        	if(s_logger.isDebugEnabled())
        		s_logger.debug("Secondary Storage Vm firewall alert, zone: " + dc.getName() + ", secStorageVm: " + 
        			secStorageVm.getHostName() + ", public IP: " + secStorageVm.getPublicIpAddress() + ", private IP: " + 
        			(secStorageVm.getPrivateIpAddress() == null ? "N/A" : secStorageVm.getPrivateIpAddress()));
    		
			_alertMgr.sendAlert(
				AlertManager.ALERT_TYPE_SSVM,
				args.getZoneId(),
				secStorageVm.getPodIdToDeployIn(),
				"Failed to open secondary storage vm firewall port. zone: " + dc.getName() + ", secStorageVm: " + secStorageVm.getHostName() 
					+ ", public IP: " + secStorageVm.getPublicIpAddress() 
					+ ", private IP: " + (secStorageVm.getPrivateIpAddress() == null ? "N/A" : secStorageVm.getPrivateIpAddress()),
				"Secondary Storage Vm alert (zone " + dc.getName() + ")"	
			);
    		break;
    	
    	case SecStorageVmAlertEventArgs.SSVM_STORAGE_ALERT :
        	if(s_logger.isDebugEnabled())
        		s_logger.debug("Secondary Storage Vm storage alert, zone: " + dc.getName() + ", secStorageVm: " + 
        			secStorageVm.getHostName() + ", public IP: " + secStorageVm.getPublicIpAddress() + ", private IP: " + 
        			secStorageVm.getPrivateIpAddress() + ", message: " + args.getMessage());
    		
			_alertMgr.sendAlert(
				AlertManager.ALERT_TYPE_STORAGE_MISC,
				args.getZoneId(),
				secStorageVm.getPodIdToDeployIn(),
				"Secondary Storage Vm storage issue. zone: " + dc.getName() + ", message: " + args.getMessage(),
				"Secondary Storage Vm alert (zone " + dc.getName() + ")"
			);
    		break;
    	}
    }
    
	@Override
	public boolean configure(String name, Map<String, Object> params)
		throws ConfigurationException {
		
		if (s_logger.isInfoEnabled())
			s_logger.info("Start configuring secondary storage vm alert manager : " + name);
		
		try {
			SubscriptionMgr.getInstance().subscribe(SecondaryStorageVmManager.ALERT_SUBJECT, this, "onSSVMAlert");
		} catch (SecurityException e) {
			throw new ConfigurationException("Unable to register secondary storage vm event subscription, exception: " + e);
		} catch (NoSuchMethodException e) {
			throw new ConfigurationException("Unable to register secondary storage vm event subscription, exception: " + e);
		}
		
		return true;
	}

	@Override
	public String getName() {
		return _name;
	}

	@Override
	public boolean start() {
		return true;
	}

	@Override
	public boolean stop() {
		return true;
	}
}
