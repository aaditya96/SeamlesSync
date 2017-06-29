# -*- Mode: Python; coding: utf-8; indent-tabs-mode: nil; tab-width: 4 -*-
### BEGIN LICENSE
# This file is in the public domain
### END LICENSE

from gi.repository import Gtk # pylint: disable=E0611

from seamlessync_lib.helpers import get_builder
#import pygtk
#import gobject
#from gi.repository import GObject

import gettext
from gettext import gettext as _
gettext.textdomain('seamlessync')

class SetupdoneDialog(Gtk.Dialog):
    __gtype_name__ = "SetupdoneDialog"
    #__gsignals__ = { 'setup-done' : (gobject.SIGNAL_RUN_LAST, gobject.TYPE_NONE,(gobject.TYPE_FLOAT,))}

    def __new__(cls):
        """Special static method that's automatically called by Python when 
        constructing a new instance of this class.
        
        Returns a fully instantiated SetupdoneDialog object.
        """
        builder = get_builder('SetupdoneDialog')
        new_object = builder.get_object('setupdone_dialog')
        new_object.finish_initializing(builder)
        return new_object

    def finish_initializing(self, builder):
        """Called when we're finished initializing.

        finish_initalizing should be called after parsing the ui definition
        and creating a SetupdoneDialog object with it in order to
        finish initializing the start of the new SetupdoneDialog
        instance.
        """
        # Get a reference to the builder and set up the signals.
        self.builder = builder
        self.ui = builder.get_ui(self)
        self.label = builder.get_object("setuplabel")

    def on_btn_ok_clicked(self, widget, data=None):
        """The user has elected to save the changes.

        Called before the dialog returns Gtk.ResponseType.OK from run().
        """
        pass

    def on_btn_cancel_clicked(self, widget, data=None):
        """The user has elected cancel changes.

        Called before the dialog returns Gtk.ResponseType.CANCEL for run()
        """
        pass

    def set_label(self, textoflabel):
        self.label.set_text(textoflabel)

#gobject.type_register(SetupdoneDialog)
#gobject.signal_new("setup-done", Sender, gobject.SIGNAL_RUN_FIRST,gobject.TYPE_NONE, ())

if __name__ == "__main__":
    dialog = SetupdoneDialog()
    dialog.show()
    Gtk.main()
