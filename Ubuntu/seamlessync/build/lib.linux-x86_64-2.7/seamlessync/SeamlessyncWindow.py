# -*- Mode: Python; coding: utf-8; indent-tabs-mode: nil; tab-width: 4 -*-
### BEGIN LICENSE
# This file is in the public domain
### END LICENSE


from locale import gettext as _

from gi.repository import Gtk # pylint: disable=E0611
from gi.repository import Notify
import logging
logger = logging.getLogger('seamlessync')

from seamlessync_lib import Window
from seamlessync.AboutSeamlessyncDialog import AboutSeamlessyncDialog
from seamlessync.PreferencesSeamlessyncDialog import PreferencesSeamlessyncDialog
from seamlessync.ConfirmdeviceDialog import ConfirmdeviceDialog
from seamlessync.SetupdoneDialog import SetupdoneDialog
from seamlessync.UnknowndeviceDialog import UnknowndeviceDialog
import seamlessync.ServerThread as ST
import seamlessync.SigEmitter as SE

import threading, os, subprocess, ConfigParser, re
YesClicked = False

# See seamlessync_lib.Window.py for more details about how this class works
donedialog = SetupdoneDialog()
#dialog = ConfirmdeviceDialog()
unknowndev = UnknowndeviceDialog()
sigEmitter = SE.SigEmitter()

class SeamlessyncWindow(Window):
    __gtype_name__ = "SeamlessyncWindow"
    #StartScript = "/etc/network/if-up.d/seamlessync-up"
    def __init__(self):
        self.ConfigFile = os.path.expanduser('~')+'/.config/seamlessync/seamlessync.cnf'
        self.config = ConfigParser.RawConfigParser()
        self.config.read(self.ConfigFile)
        self.started = False
        self.WindowResponsive = True
        self.n = Notify.Notification.new("")

    def finish_initializing(self, builder): # pylint: disable=E1002
        """Set up the main window"""
        super(SeamlessyncWindow, self).finish_initializing(builder)

        self.AboutDialog = AboutSeamlessyncDialog
        self.PreferencesDialog = PreferencesSeamlessyncDialog

        p = subprocess.Popen("ip addr | grep inet[^6] | grep -v 127.0.0.1 | awk {'print $2'} | sed 's!/[0-9]*!!g'", stdout=subprocess.PIPE, shell=True)
        (output, err) = p.communicate()
        MyIP = output.rstrip()
        
        self.startbutton = self.builder.get_object("StartButton")
        self.ipaddress = self.builder.get_object("IPAddress")
        self.port = self.builder.get_object("PortNumber")

        self.ipaddress.set_text(MyIP)
        self.ipaddress.editable = (0)
        self.port.set_text(self.config.get("local","port"))
        Notify.init("Seamlessync")

        #donedialog.connect("response",self.close_and_start_daemon)
        #unknowndev.connect("response",self.close_window)
        # Code for other initialization actions should be added here.
        sigEmitter.connect("setup-done",self.close_and_start_daemon)
        sigEmitter.connect("unknown-found",self.close_window)

    def on_StartButton_clicked(self,widget):

        if self.WindowResponsive:
            self.config.set("local","port",self.port.get_text())
            with open(self.ConfigFile, 'wb') as configfile:
                self.config.write(configfile)
            if(self.started == False):
                #p = subprocess.Popen(self.StartScript)
                flag = threading.Event()
                self.sthread = ST.serverthread(self.ipaddress.get_text(),self.port.get_text(),self.config.get("local","dir"))
                self.sthread.start(flag)
                flag.wait()
                self.started = True
                self.n.update("SeamlesSync", "Connection Initiated", "/usr/share/seamlessync/media/background.png")
                self.n.show()

    def on_CancelButton_clicked(self,widget):
        
        if self.WindowResponsive:
            if(self.started == True):
                self.sthread.stop()
                self.started = False
                self.n.update("SeamlesSync", "Connection Cancelled", "/usr/share/seamlessync/media/background.png")
                self.n.show()

    def on_destroy(self, widget, data=None):
        """Called when the SeamlessyncWindow is closed."""
        # Clean up code for saving application state should be added here.
        if(self.started == True):
            #p = subprocess.Popen(self.StopScript)
            self.sthread.stop()
            self.started = False
            self.n.update("SeamlesSync", "Connection Closed,Exiting", "/usr/share/seamlessync/media/background.png")
            self.n.show()
        os.system("sleep 1")
        Gtk.main_quit()

    def close_and_start_daemon(self,a,b):
        if(self.started == True):
            self.sthread.stop()
            self.started = False
        os.system("sleep 2")
        #p = subprocess.Popen("gksudo -m 'Provide your password (required for first time only)' "+self.StartScript, stdout=subprocess.PIPE, shell=True)
        os.system("nmcli nm wifi off")
        os.system("nmcli nm wifi on")
        self.WindowResponsive = False

        #donedialog2 = SetupdoneDialog()
        #donedialog2.set_label("Setup done properly. Click OK to exit")
        #res = donedialog2.run()
        #donedialog2.hide()
        
        #donedialog2.destroy()
        
        self.destroy()

    def close_window(self,a,b):
        #if b== Gtk.ResponseType.CANCEL:
            if(self.started == True):
                self.sthread.stop()
                self.started = False
            os.system("sleep 1")
            self.destroy()
