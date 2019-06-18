### General
* Setup manager initialization so that only one manager implements default search 
at a time. Example, in JM, FM and PM should not implement default searching since
it is already implemented by JM. Implement a method that adds a single listener 
by removing all existing listener before adding a new listener.
