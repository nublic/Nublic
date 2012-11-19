'''
:author Alejandro Serrano <alex@nublic.com>
'''

import logging
from pykka.actor import ThreadingActor
import traceback

class Processor(ThreadingActor):
    '''
    Defines a processor for watcher events
    '''
    
    def __init__(self, name, watcher, throwException, logger = None):
        '''
        Creates a new processor
        
        :param name: the processor id
        :type name: string
        :param watcher: the parent watcher for this processor
        :type name: a FileWatcher instance
        :param throwException: whether to throw an exception or log errors
        :type throwException: boolean
        '''
	super(Processor, self).__init__()
        self._name = name
        self._watcher = watcher
        self._throwException = throwException
        self._logger = logger
    
    def process(self, change):
        '''
        Processes a change in the file system
        
        :param change: the change to respond to
        :type change: a FileChange instance
        '''
        raise NotImplementedError("Should be implemented in derived classes")
    
    def get_logger(self):
        '''
        Gets the logger object associated to this processor
        '''
        return self._logger
    
    def on_receive(self, message):
        '''
        Callback as an actor
        
        :type message: a ForwardFileChange instance
        '''
        if 'change' in message and 'id' in message:
            try:
                self.process(message.get('change'))
            except BaseException as e:
                if self._throwException:
                    raise
                else:
                    if self._logger != None:
                        self._logger.error('ERROR in %s PROCESSOR: %s\n%s', self._name, str(e), traceback.format_exc())
            # Tell back the parent watcher
            #self._watcher.tell({'command': 'back', 'app_name': self._name,
            #                    'id': message.get('id'), 'change': message.get('change')})
        else:
            if self._logger != None:
                self._logger.error('Message without change or id: %s', str(message))
            
