# -*- Mode: Python; coding: utf-8; indent-tabs-mode: nil; tab-width: 4 -*-
### BEGIN LICENSE
# This file is in the public domain
### END LICENSE

from gi.repository import Gtk # pylint: disable=E0611

from seamlessync_lib.helpers import get_builder

import gettext
from gettext import gettext as _
gettext.textdomain('seamlessync')

class ConfirmdeviceDialog(Gtk.Dialog):
    __gtype_name__ = "ConfirmdeviceDialog"

    def __new__(cls):
        """Special static method that's automatically called by Python when 
        constructing a new instance of this class.
        
        Returns a fully instantiated ConfirmdeviceDialog object.
        """
        builder = get_builder('ConfirmdeviceDialog')
        new_object = builder.get_object('confirmdevice_dialog')
        new_object.finish_initializing(builder)
        return new_object

    def finish_initializing(self, builder):
        """Called when we're finished initializing.

        finish_initalizing should be called after parsing the ui definition
        and creating a ConfirmdeviceDialog object with it in order to
        finish initializing the start of the new ConfirmdeviceDialog
        instance.
        """
        # Get a reference to the builder and set up the signals.
        self.builder = builder
        self.ui = builder.get_ui(self)
        self.label = builder.get_object("InfoLabel")

    def on_YesButton_clicked(self, widget, data=None):
        """The user has elected to save the changes.

        Called before the dialog returns Gtk.ResponseType.OK from run().
        """
        pass

    def on_NoButton_clicked(self, widget, data=None):
        """The user has elected cancel changes.

        Called before the dialog returns Gtk.ResponseType.CANCEL for run()
        """
        pass
    def set_label(self,mac, ip):
        self.label.set_text("A device is trying to connect\nMAC Address: "+mac+"\nIP Address: "+ip+"\n\nIs it your device?")


if __name__ == "__main__":
    dialog = ConfirmdeviceDialog()
    dialog.show()
    Gtk.main()
