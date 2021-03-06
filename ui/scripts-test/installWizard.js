(function($, cloudStack, testData) {
  cloudStack.installWizard = {
    // Check if install wizard should be invoked
    check: function(args) {
      args.response.success({
        doInstall: args.context.users[0].username == 'newuser'
      });
    },

    changeUser: function(args) {
      setTimeout(function() {
        args.response.success({
          data: {
            newUser: {
              username: args.data.username
            }
          }
        });
      }, 500);
    },

    // Copy text
    copy: {
      // Tooltips
      'tooltip.addZone.name': function(args) {
        args.response.success({
          text: 'A name for the zone.'
        });
      },

      'tooltip.addZone.dns1': function(args) {
        args.response.success({
          text: 'These are DNS servers for use by guest VMs in the zone. These DNS servers will be accessed via the public network you will add later. The public IP addresses for the zone must have a route to the DNS server named here.'
        });
      },

      'tooltip.addZone.dns2': function(args) {
        args.response.success({
          text: 'These are DNS servers for use by guest VMs in the zone. These DNS servers will be accessed via the public network you will add later. The public IP addresses for the zone must have a route to the DNS server named here.'
        });
      },

      'tooltip.addZone.internaldns1': function(args) {
        args.response.success({
          text: 'These are DNS servers for use by system VMs in the zone. These DNS servers will be accessed via the private network interface of the System VMs. The private IP address you provide for the pods must have a route to the DNS server named here.'
        });
      },

      'tooltip.addZone.internaldns2': function(args) {
        args.response.success({
          text: 'These are DNS servers for use by system VMs in the zone. These DNS servers will be accessed via the private network interface of the System VMs. The private IP address you provide for the pods must have a route to the DNS server named here.'
        });
      },

      'tooltip.addGuestNetwork.name': function(args) {
        args.response.success({
          text: 'A name for your network'
        });
      },

      'tooltip.addGuestNetwork.description': function(args) {
        args.response.success({
          text: 'A description for your network'
        });
      },

      'tooltip.addGuestNetwork.guestGateway': function(args) {
        args.response.success({
          text: 'The gateway that the guests should use'
        });
      },

      'tooltip.addGuestNetwork.guestNetmask': function(args) {
        args.response.success({
          text: 'The netmask in use on the subnet that the guests should use'
        });
      },

      'tooltip.addGuestNetwork.guestStartIp': function(args) {
        args.response.success({
          text: 'The range of IP addresses that will be available for allocation to guests in this zone.  If one NIC is used, these IPs should be in the same CIDR as the pod CIDR.'
        });
      },

      'tooltip.addGuestNetwork.guestEndIp': function(args) {
        args.response.success({
          text: 'The range of IP addresses that will be available for allocation to guests in this zone.  If one NIC is used, these IPs should be in the same CIDR as the pod CIDR.'
        });
      },

      'tooltip.addPod.name': function(args) {
        args.response.success({
          text: 'A name for the pod'
        });
      },

      'tooltip.addPod.gateway': function(args) {
        args.response.success({
          text: 'The gateway for the hosts in that pod.'
        });
      },

      'tooltip.addPod.netmask': function(args) {
        args.response.success({
          text: 'The netmask in use on the subnet the guests will use.'
        });
      },

      'tooltip.addPod.startip': function(args) {
        args.response.success({
          text: 'This is the IP range in the private network that the CloudStack uses to manage Secondary Storage VMs and Console Proxy VMs. These IP addresses are taken from the same subnet as computing servers.'
        });
      },

      'tooltip.addPod.endip': function(args) {
        args.response.success({
          text: 'This is the IP range in the private network that the CloudStack uses to manage Secondary Storage VMs and Console Proxy VMs. These IP addresses are taken from the same subnet as computing servers.'
        });
      },

      'tooltip.addCluster.name': function(args) {
        args.response.success({
          text: 'A name for the cluster.  This can be text of your choosing and is not used by CloudStack.'
        });
      },

      'tooltip.addHost.hostname': function(args) {
        args.response.success({
          text: 'The DNS name or IP address of the host.'
        });
      },

      'tooltip.addHost.username': function(args) {
        args.response.success({
          text: 'Usually root.'
        });
      },

      'tooltip.addHost.password': function(args) {
        args.response.success({
          text: 'This is the password for the user named above (from your XenServer install).'
        });
      },

      'tooltip.addPrimaryStorage.name': function(args) {
        args.response.success({
          text: 'The name for the storage device.'
        });
      },

      'tooltip.addPrimaryStorage.server': function(args) {
        args.response.success({
          text: '(for NFS, iSCSI, or PreSetup) The IP address or DNS name of the storage device.'
        });
      },

      'tooltip.addPrimaryStorage.path': function(args) {
        args.response.success({
          text: '(for NFS) In NFS this is the exported path from the server. Path (for SharedMountPoint).  With KVM this is the path on each host that is where this primary storage is mounted.  For example, "/mnt/primary".'
        });
      },

      'tooltip.addSecondaryStorage.nfsServer': function(args) {
        args.response.success({
          text: 'The IP address of the NFS server hosting the secondary storage'
        });
      },

      'tooltip.addSecondaryStorage.path': function(args) {
        args.response.success({
          text: 'The exported path, located on the server you specified above'
        });
      },

      // Intro text
      whatIsCloudStack: function(args) {
        args.response.success({
          text: 'CloudStack&#8482 is a software platform that pools computing resources to build public, private, and hybrid Infrastructure as a Service (IaaS) clouds. CloudStack&#8482 manages the network, storage, and compute nodes that make up a cloud infrastructure. Use CloudStack&#8482 to deploy, manage, and configure cloud computing environments.<br/><br/>Extending beyond individual virtual machine images running on commodity hardware, CloudStack&#8482 provides a turnkey cloud infrastructure software stack for delivering virtual datacenters as a service - delivering all of the essential components to build, deploy, and manage multi-tier and multi-tenant cloud applications. Both open-source and Premium versions are available, with the open-source version offering nearly identical features. '
        });
      },

      whatIsAZone: function(args) {
        args.response.success({
          text: 'A zone is the largest organizational unit within a CloudStack&#8482; deployment. A zone typically corresponds to a single datacenter, although it is permissible to have multiple zones in a datacenter. The benefit of organizing infrastructure into zones is to provide physical isolation and redundancy. For example, each zone can have its own power supply and network uplink, and the zones can be widely separated geographically (though this is not required).'
        });
      },

      whatIsAPod: function(args) {
        args.response.success({
          text: 'A pod often represents a single rack. Hosts in the same pod are in the same subnet.<br/><br/>A pod is the second-largest organizational unit within a CloudStack&#8482; deployment. Pods are contained within zones. Each zone can contain one or more pods; in the Basic Installation, you will have just one pod in your zone'
        });
      },

      whatIsACluster: function(args) {
        args.response.success({
          text: 'A cluster provides a way to group hosts. The hosts in a cluster all have identical hardware, run the same hypervisor, are on the same subnet, and access the same shared storage. Virtual machine instances (VMs) can be live-migrated from one host to another within the same cluster, without interrupting service to the user. A cluster is the third-largest organizational unit within a CloudStack&#8482; deployment. Clusters are contained within pods, and pods are contained within zones.<br/><br/>CloudStack&#8482; allows multiple clusters in a cloud deployment, but for a Basic Installation, we only need one cluster. '
        });
      },

      whatIsAHost: function(args) {
        args.response.success({
          text: 'A host is a single computer. Hosts provide the computing resources that run the guest virtual machines. Each host has hypervisor software installed on it to manage the guest VMs (except for bare metal hosts, which are a special case discussed in the Advanced Installation Guide). For example, a Linux KVM-enabled server, a Citrix XenServer server, and an ESXi server are hosts. In a Basic Installation, we use a single host running XenServer.<br/><br/>The host is the smallest organizational unit within a CloudStack&#8482; deployment. Hosts are contained within clusters, clusters are contained within pods, and pods are contained within zones. '
        });
      },

      whatIsPrimaryStorage: function(args) {
        args.response.success({
          text: 'A CloudStack&#8482; cloud infrastructure makes use of two types of storage: primary storage and secondary storage. Both of these can be iSCSI or NFS servers, or localdisk.<br/><br/><strong>Primary storage</strong> is associated with a cluster, and it stores the disk volumes of each guest VM for all the VMs running on hosts in that cluster. The primary storage server is typically located close to the hosts. '
        });
      },

      whatIsSecondaryStorage: function(args) {
        args.response.success({
          text: 'Secondary storage is associated with a zone, and it stores the following:<ul><li>Templates - OS images that can be used to boot VMs and can include additional configuration information, such as installed applications</li><li>ISO images - OS images that can be bootable or non-bootable</li><li>Disk volume snapshots - saved copies of VM data which can be used for data recovery or to create new templates</ul>'
        });
      }
    },

    action: function(args) {
      var complete = args.response.success;
      var error = args.response.error;
      var message = args.response.message;
      var startFn = args.startFn;

      var createZone = function(args) {
        message('Creating zone');
        setTimeout(function() {
          createPod();
        }, 500);
      };

      var createPod = function(args) {
        message('Creating pod');
        setTimeout(function() {
          createIPRange();
        }, 500);
      };

      var createIPRange = function(args) {
        message('Creating network');
        setTimeout(function() {
          createCluster();
        }, 500);
      };

      var createCluster = function(args) {
        message('Creating cluster');
        setTimeout(function() {
          createHost();
          //error('addCluster', 'Could not create cluster.', createPod);
        }, 500);
      };

      var createHost = function(args) {
        message('Creating host');
        setTimeout(function() {
          createPrimaryStorage();
        }, 500);
      };

      var createPrimaryStorage = function(args) {
        message('Creating primary storage');
        setTimeout(function() {
          createSecondaryStorage();
        }, 500);
      };

      var createSecondaryStorage = function(args) {
        message('Creating secondary storage');
        setTimeout(function() {
          pollSystemVMs();          
        }, 500);
      };

      var pollSystemVMs = function() {
        message('Creating system VMs (this may take a while)');
        setTimeout(complete, 5000);
      };

      if (startFn) {
        startFn();
      } else {
        createZone();        
      }
    }
  };
}(jQuery, cloudStack, testData));
