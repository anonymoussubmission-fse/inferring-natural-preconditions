class Trace:
    def __init__(self, test_num, exception_type, location):
        self.exception_type = exception_type
        self.location = location

class Location:
    def __init__(self, _class, method, lineno):
        self._class = _class
        self.method = method
        self.lineno = lineno
