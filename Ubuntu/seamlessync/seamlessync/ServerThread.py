from gi.repository import Gtk,Gdk # pylint: disable=E0611

from pyftpdlib.authorizers import DummyAuthorizer
from pyftpdlib.handlers import FTPHandler
from pyftpdlib.servers import FTPServer
from pyftpdlib.filesystems import AbstractedFS

from seamlessync_lib import Window
from seamlessync.ConfirmdeviceDialog import ConfirmdeviceDialog
from seamlessync.SetupdoneDialog import SetupdoneDialog
import seamlessync.ServerThread
import seamlessync.SeamlessyncWindow

import threading, os, subprocess, ConfigParser, re
firstRequest = False


'''
class FTPFS(AbstractedFS):
	def FTPFS(self,dirc,handler):
		AbstractedFS.__init__(self,dirc,handler)

	def GetCWD(self):
		return cwd()
'''

class MyHandler(FTPHandler):
    	ConfigFile = os.path.expanduser('~')+'/.config/seamlessync/seamlessync.cnf'
    
	def on_connect(self):
        	config = ConfigParser.RawConfigParser()
        	config.read(self.ConfigFile)
		#print "%s:%s connected" % (self.remote_ip, self.remote_port)
        	if config.getboolean("init-setup","flag") == False:
            		p = subprocess.Popen("ping -c 1 "+self.remote_ip.rstrip(),stdout=subprocess.PIPE, shell=True)
		    	p = subprocess.Popen("arp -an | grep "+self.remote_ip.rstrip()+" | awk {'print $4'}",stdout=subprocess.PIPE, shell=True)
		    	(output, err) = p.communicate()
		    	RemoteMAC = output.rstrip()
		
		    	LegitRemoteMAC = config.get("MAC","remote-device")
		    #LegitAPMAC = self.config.get("MAC","access-point")
		    	if RemoteMAC != LegitRemoteMAC :
			    self.respond("Connection refused!")
			    self.close_when_done()
        #print "connected"

	def on_disconnect(self):
		# do something when client disconnects
		#print "in disconnect"
		#print "yo"

		'''
		if(FTPHandler.fs == None):
			print "it's none"
		elif (FTPHandler.fs != None):
			print "It's not none"
		'''

		'''
		if(self.abstracted_fs == None ):
			print "its none"
		elif (self.abstracted_fs != None):
			print "its not none"

		self.current = self.abstracted_fs.cwd()
		print self.current
		'''

		'''		
		self.cwd = FTPHandler.fs.cwd
		print "After cwd"
		print self.cwd
        	assert isinstance(self.cwd, unicode), self.cwd
		print "After isinstance"
		print self.cwd
		print '"%s" is the current directory.' % self.cwd.replace('"', '""')
		print "After"
        	'''
		pass

	def on_login(self, username):
		# do something when user login
        	config = ConfigParser.RawConfigParser()
        	config.read(self.ConfigFile)
		Gdk.threads_init()
            	Gdk.threads_enter()
        	if config.getboolean("init-setup","flag") == True:
            		#print "logged in"
            		p = subprocess.Popen("ping -c 1 "+self.remote_ip.rstrip(),stdout=subprocess.PIPE, shell=True)
            		p = subprocess.Popen("arp -an | grep "+self.remote_ip.rstrip()+" | awk {'print $4'}",stdout=subprocess.PIPE, shell=True)
            		(output, err) = p.communicate()
            		RemoteMAC = output.rstrip()
            
            		#confirm device from user
            		#print RemoteMAC
            		#Gdk.threads_init()
            		#Gdk.threads_enter()

			dialog = ConfirmdeviceDialog()
            		dialog.set_label(RemoteMAC,self.remote_ip.rstrip())
            		result = dialog.run()
            		dialog.hide()
            		if result == Gtk.ResponseType.OK:
                		p = subprocess.Popen("route | grep 'default' | awk {'print $2'}",stdout=subprocess.PIPE, shell=True)
                		(output, err) = p.communicate()
                		MyAPIP = output.rstrip()
        
                		p = subprocess.Popen("arp -a "+MyAPIP ,stdout=subprocess.PIPE, shell=True)
                		(output, err) = p.communicate()
                		pr = re.compile(ur'(?:[0-9a-fA-F]:?){12}')
                		MyAPMAC = re.findall(pr,output.rstrip())[0]

                		#self.config.set("local","port",self.port.get_text())
                		config.set("local","gateway",MyAPIP)
                		config.set("MAC","access-point",MyAPMAC)
                		config.set("MAC","remote-device",RemoteMAC)
                		config.set("init-setup","flag","false")
                		with open(self.ConfigFile, 'wb') as configfile:
                		    config.write(configfile)

                		#s = seamlessync.SeamlessyncWindow.donedialog.run()
                		#seamlessync.SeamlessyncWindow.donedialog.hide()
				global YesClicked
				YesClicked = True
                
            		elif result == Gtk.ResponseType.CANCEL:
                		self.respond("Connection refused!")
                		self.close_when_done()
				#r = seamlessync.SeamlessyncWindow.unknowndev.run()
				#seamlessync.SeamlessyncWindow.unknowndev.hide()
				global YesClicked
				YesClicked = False

            		#Gdk.threads_leave()
        	else:
            		pass

		if YesClicked :
			seamlessync.SeamlessyncWindow.donedialog.set_label("Your device configurations are saved")
			s = seamlessync.SeamlessyncWindow.donedialog.run()
                	seamlessync.SeamlessyncWindow.donedialog.hide()
			if s == Gtk.ResponseType.OK:
				seamlessync.SeamlessyncWindow.donedialog.destroy()
				seamlessync.SeamlessyncWindow.sigEmitter.emitSignal('setup-done')
		else :
			r = seamlessync.SeamlessyncWindow.unknowndev.run()
			seamlessync.SeamlessyncWindow.unknowndev.hide()
			if r == Gtk.ResponseType.OK:
				seamlessync.SeamlessyncWindow.unknowndev.destroy()
				seamlessync.SeamlessyncWindow.sigEmitter.emitSignal('unknown-found')

		Gdk.threads_leave()

class serverthread(threading.Thread):
    def __init__(self,ip,port,directory):
        threading.Thread.__init__(self)
        self.ip = ip
        self.port = port
        self.directory = directory
        self.active = False
        
    def start(self, flag=None):
        if(self.active == False):
            self.flag = flag
            threading.Thread.start(self)

    def run(self):
        if(self.active == False):
            authorizer = DummyAuthorizer()
            authorizer.add_user('user', 'pass', self.directory, perm='elradfmwM')
            handler = MyHandler
	    #ftpfs = FTPFS('/home/aaditya/ESDLproject/serverdata',handler)
            handler.authorizer = authorizer
            handler.banner = "pyftpdlib based ftpd ready."
            address = (self.ip, self.port)
            self.server = FTPServer(address, handler)
            self.server.max_cons = 256
            self.server.max_cons_per_ip = 5
            if self.flag:
                self.flag.set()
                self.active = True
                while self.active:
                    self.server.serve_forever(timeout = 1, blocking = False)
                self.server.close_all()

    def stop(self):
        #self.server.close_all()
        if(self.active == True):
            #print "stopping"
            self.active = False
