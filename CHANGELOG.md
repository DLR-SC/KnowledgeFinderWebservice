# 2.0.0

## 2018-07-06

### changes

* Handle the json return format of solr 7.2.0
* Maven builds are now generated in the configuration projects
* portlet: major refactoring of the code structure
   * each panel now only handles the drawing, events are forwarded to the pageManger
   * the pageManager handles all events, calls if necessary the connector to request new information form the webservice and forwards the information from the server to the panels for GUI updates
   * the connector builds the query url and sends the request to the webservice 
   * the urlManager allows to create and manipulate a url
   * there is now a knowledgefinder namespace
   * pageManager, urlManager and connector are now modules


### bug fixes

* Date range filter query: filter all entries where field is empty
* Remove the last references to the projects working title "kownledgefinderII"

# 1.0.0

## 2017-03-10

### new (initial release) - webservice

* getDocuments
* getNodes
* exportDocuments in BibTeX
* Role Based Access Control

### new (initial release) - portlet

* Free text search
* Full-text search
* Facets filter
* Date range filter
* Metadata graph
* Export metadata
