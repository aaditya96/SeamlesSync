# -*- Mode: Python; coding: utf-8; indent-tabs-mode: nil; tab-width: 4 -*-
### BEGIN LICENSE
# This file is in the public domain
### END LICENSE
#import sys,os
#sys.path.append('/home/aaditya/seamlessync')

#sys.path.insert(0, '/home/aaditya/seamlessync/')
#os.putenv('PYTHONPATH', '/home/aaditya/seamlessync/')


import optparse
import ConfigParser,os

from locale import gettext as _

from gi.repository import Gtk # pylint: disable=E0611

from seamlessync import SeamlessyncWindow
from seamlessync.SetupdoneDialog import SetupdoneDialog

from seamlessync_lib import set_up_logging, get_version
from seamlessync.ForgetconfigDialog import ForgetconfigDialog
import subprocess
from random import randint

homepath = os.path.expanduser('~')
configcontent = "[init-setup]\nflag = true\n\n[MAC]\nremote-device = \naccess-point = \
\n\n[local]\ndir = \nport = \ngateway = \n\n[default]\nport = 5555\n"

def parse_options():
    """Support for command line options"""
    parser = optparse.OptionParser(version="%%prog %s" % get_version())
    parser.add_option(
        "-v", "--verbose", action="count", dest="verbose",
        help=_("Show debug messages (-vv debugs seamlessync_lib also)"))
    (options, args) = parser.parse_args()

    set_up_logging(options)

def main():
    'constructor for your class instances'
    parse_options()
    
    '''
    ConfigFile = "/etc/seamlessync/seamlessync.cnf"
    config = ConfigParser.ConfigParser()
    config.read(ConfigFile)
    initvalue = config.getboolean("init-setup","flag")
    '''
    ConfigFile = homepath+'/.config/seamlessync/seamlessync.cnf'
    if os.path.isfile(ConfigFile):
        config = ConfigParser.ConfigParser()
        config.read(ConfigFile)
        initvalue = config.getboolean("init-setup","flag")
    else:
        initvalue = True
        if not os.path.exists(homepath+'/.config/seamlessync'):
            os.makedirs(homepath+'/.config/seamlessync')
        with open(homepath+'/.config/seamlessync/seamlessync.cnf','wb') as newconf:
            newconf.write(str(configcontent))
        
            
    # Run the application.    
    if(initvalue == True ):
        
        folderchoose = SetupdoneDialog()
        folderchoose.set_label("Please choose a folder to contain \nbacked up data from your android device")
        result = folderchoose.run()
        folderchoose.hide()
        if(result == Gtk.ResponseType.OK):
        
            folderchoose.destroy()
            folderdialog = Gtk.FileChooserDialog("Please choose a folder", folderchoose,Gtk.FileChooserAction.SELECT_FOLDER,(Gtk.STOCK_CANCEL, Gtk.ResponseType.CANCEL,"Select", Gtk.ResponseType.OK))
            folderdialog.set_default_size(700, 300)

            response = folderdialog.run()
            if response == Gtk.ResponseType.OK:
                config = ConfigParser.ConfigParser()
                config.read(ConfigFile)
                config.set("local","dir",folderdialog.get_filename())
                with open(ConfigFile, 'wb') as configfile:
                    config.write(configfile)

            elif response == Gtk.ResponseType.CANCEL:
                folderdialog.destroy()
                return

            folderdialog.destroy()

            #folderchoose.destroy()

            newport = config.get("default","port")
            while True:
                p = subprocess.Popen("fuser "+newport+"/tcp",stdout=subprocess.PIPE, shell=True)
                (output, err) = p.communicate()
		        portoutput = output.rstrip()
                if(portoutput == ""):
                    break;
                newport = str(randint(1025,65534))

            config = ConfigParser.ConfigParser()
            config.read(ConfigFile)
            config.set("local","port",newport)
            with open(ConfigFile, 'wb') as configfile:
                config.write(configfile)

            window = SeamlessyncWindow.SeamlessyncWindow()
            window.show()
            #folderdialog.destroy()
            #folderchoose.destroy()
            Gtk.main()
        else:
            pass

    else:
        fcd = ForgetconfigDialog()
        result = fcd.run()
        fcd.hide()
        if result == Gtk.ResponseType.OK:
            p = subprocess.Popen("/etc/network/if-post-down.d/seamlessync-down", stdout=subprocess.PIPE, shell=True)
            config = ConfigParser.ConfigParser()
            config.read(ConfigFile)
            config.set("MAC","remote-device","")
            config.set("MAC","access-point","")
            config.set("local","port","")
            config.set("local","gateway","")
            config.set("local","dir","")
            config.set("init-setup","flag","true")
            config.set("default","port",5555)
            with open(ConfigFile, 'wb') as configfile:
                    config.write(configfile)

