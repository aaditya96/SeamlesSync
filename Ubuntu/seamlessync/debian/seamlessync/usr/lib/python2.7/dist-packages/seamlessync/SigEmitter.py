import pygtk
import gobject

class SigEmitter(gobject.GObject):
	__gsignals__ = {'unknown-found' : (gobject.SIGNAL_RUN_LAST, gobject.TYPE_NONE,(int,)) , 'setup-done' : (gobject.SIGNAL_RUN_LAST, gobject.TYPE_NONE,(int,))}
	
	def __init__(self):
		gobject.GObject.__init__(self)

	def emitSignal(self, signame):
		self.emit(signame,1)

gobject.type_register(SigEmitter)
