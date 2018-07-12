(function (context, connector, StateUrl) {
    //------------------------------------------------------------------------------------------------------------------
    //  init
    //------------------------------------------------------------------------------------------------------------------
    // init data -------------------------------------------------------------------------------------------------------
    var pageData = {
        fields: {
            detailView: null,
            detailViewHighlight: null,
            resultListEntry: null,
            resultListEntryHighlight: null,
            export: {}
        },
        facetsDataList: allFacets,
        fieldsDict: allFields,
        url: null,
        hideGraph: filterConfig.graph.hide
    };

    // Configuration loaded via index.jsp:
    // baseUrl, filterConfig, detailViewConfig, resultConfig, exportConfig, allFacets, allFields
    for (var field in pageData.fieldsDict) {
        if (pageData.fieldsDict.hasOwnProperty(field)) {
            var id = pageData.fieldsDict[field];
            pageData.facetsDataList[id + "__ANY"] = pageData.facetsDataList[id];
        }
    }

    //set which fields needs to be queried to draw resultList/detailView -------------------------------------------
    function extractFieldNames(config) {
        var highlightFiled = [], standardField = [];
        for (var property in config) {
            if (!Array.isArray(config[property])) {
                if (config[property].highlight)
                    highlightFiled.push(config[property].field);
                standardField.push(config[property].field);
            } else {
                for (var groupIndex = 0; groupIndex < config.body.length; groupIndex++) {
                    for (var fieldIndex = 0; fieldIndex < config.body[groupIndex].length; fieldIndex++) {
                        if (config.body[groupIndex][fieldIndex].highlight)
                            highlightFiled.push(config.body[groupIndex][fieldIndex].field);
                        standardField.push(config.body[groupIndex][fieldIndex].field);
                    }
                }
            }
        }
        return [standardField.join(","), highlightFiled.join(",")];
    }

    var fieldNames = extractFieldNames(resultConfig);
    pageData.fields.resultListEntry = fieldNames[0];
    pageData.fields.resultListEntryHighlight = fieldNames[1];

    fieldNames = extractFieldNames(detailViewConfig);
    pageData.fields.detailView = fieldNames[0];
    pageData.fields.detailViewHighlight = fieldNames[1];

    // export fields -----------------------------------------------------------------------------------------------
    for (var exportType in exportConfig)
        if (exportConfig.hasOwnProperty(exportType))
            pageData.fields.export[exportType] = exportConfig[exportType].fields.join(",");

    // init panels and views -------------------------------------------------------------------------------------------
    var subElements = {
        graphPanel: initGraphPanel(),
        facetsFilterPanel: initFacetsFilterPanel(filterConfig.facets),
        dateRangeFilterPanel: initRangeFilterPanel(filterConfig.dateRange),
        resultPanel: initResultPanel(resultConfig, exportConfig),
        detailView: initDetailView(detailViewConfig),
        selectionPanel: initSelectionPanel()
    };

    // submit FreeText -------------------------------------------------------------------------------------------------
    d3.select("#freetextform").on("submit", function () { //TODO tidy up
        d3.event.preventDefault();
        var textVal = d3.select("#search-field");
        var value = textVal.property("value");
        if (value) {
            context.updatePage(applyFreeTextFilterToURL(value, true));
            textVal.property("value", "");
        }
        return false;
    });

    function createInitialStateURL() {
        // Configuration loaded via index.jsp
        // set initial query values ------------------------------------------------------------------------------------
        var highLevelGroups = [], lowLevelGroups = [];
        for (var filterIndex = 0; filterIndex < filterConfig.facets.length; filterIndex++) {
            var filter = filterConfig.facets[filterIndex];
            if (!filter.collapsed) {
                if (filter.subItems.length > 0)
                    for (var subFilterIndex = 0; subFilterIndex < filter.subItems.length; subFilterIndex++) {
                        highLevelGroups.push(filter.subItems[subFilterIndex].subItemsFacet);
                        if (!filter.subItems[subFilterIndex].collapsed)
                            lowLevelGroups.push(filter.subItems[subFilterIndex].subItemsFacet);
                    }
                else
                    lowLevelGroups.push(filter.subItemsFacet);
            }
        }

        var sort = null;
        for (var sortOptionIndex = 0; sortOptionIndex < resultConfig.sortOptions.length; sortOptionIndex++)
            if (resultConfig.sortOptions[sortOptionIndex].default === true)
                sort = resultConfig.sortOptions[sortOptionIndex].value;

        // initial query
        var stateUrl = new StateUrl(window.location.href);
        if (stateUrl.getParameter(StateUrl.params.query) === undefined)
            stateUrl.setParameter(StateUrl.params.query, "");
        if (stateUrl.getParameter(StateUrl.params.filterQuery) === undefined)
            stateUrl.setParameter(StateUrl.params.filterQuery, "");

        // initial values facets/show/hide
        if (stateUrl.getParameter(StateUrl.params.lowLevelGroups) === undefined)
            stateUrl.setParameter(StateUrl.params.lowLevelGroups, lowLevelGroups.join(","));
        if (stateUrl.getParameter(StateUrl.params.highLevelGroups) === undefined)
            stateUrl.setParameter(StateUrl.params.highLevelGroups, highLevelGroups.join(","));

        // pagination
        if (stateUrl.getParameter(StateUrl.params.start) === undefined)
            stateUrl.setParameter(StateUrl.params.start, resultConfig.pages.initial);
        if (stateUrl.getParameter(StateUrl.params.rows) === undefined)
            stateUrl.setParameter(StateUrl.params.rows, resultConfig.pages.rows);
        // result sorting
        if (stateUrl.getParameter(StateUrl.params.sort) === undefined)
            stateUrl.setParameter(StateUrl.params.sort, sort);

        return stateUrl.data.url;
    }

    context.getCurrentStateUrl = function () {
        if (!pageData.url)
            return createInitialStateURL();
        return pageData.url;
    };

    function initGraphPanel() {
        // Configuration loaded via index.jsp
        var graphPanel = new knowledgefinder.GraphPanel("#graph1", pageData.fieldsDict, pageData.facetsDataList, filterConfig.graph.nodes.maxNumber);
        graphPanel.initGraph();
        graphPanel.addEventListener("add", function (listId) {
            context.updatePage(applyFacetsFilterToURL(listId, true, pageData.facetsDataList));
        });
        graphPanel.addEventListener("mouseoverNode", function (id) {
            highlightFacetFilter(id, true);
        });
        graphPanel.addEventListener("mouseoutNode", function (id) {
            highlightFacetFilter(id, false);
        });
        graphPanel.addEventListener("mouseoverEdge", function (edge) {
            highlightEdge(edge, true);
        });
        graphPanel.addEventListener("mouseoutEdge", function (edge) {
            highlightEdge(edge, false);
        });
        graphPanel.hide(pageData.hideGraph);
        return graphPanel;
    }

    function initFacetsFilterPanel(facetFilterConfig) {
        var facetsFilterPanel = new knowledgefinder.FacetsFilterPanel("#selecttable", pageData.facetsDataList, pageData.fieldsDict, facetFilterConfig);
        facetsFilterPanel.addEventListener("remove", function (id) {
            context.updatePage(applyFacetsFilterToURL([id], false, pageData.facetsDataList));
        });
        facetsFilterPanel.addEventListener("add", function (id) {
            context.updatePage(applyFacetsFilterToURL([id], true, pageData.facetsDataList));
        });
        facetsFilterPanel.addEventListener("mouseover", function (id) {
            highlightFacetFilter(id, true);
        });
        facetsFilterPanel.addEventListener("mouseout", function (id) {
            highlightFacetFilter(id, false);
        });
        facetsFilterPanel.addEventListener("mouseoverFacetsFilter", function (facetsFilterId) {
            highlightFacetsFilter(facetsFilterId, true);
        });
        facetsFilterPanel.addEventListener("mouseoutFacetsFilter", function (facetsFilterId) {
            highlightFacetsFilter(facetsFilterId, false);
        });
        facetsFilterPanel.addEventListener("displayFacetsFilter", function (event) {
            context.updatePage(applyDisplayFacetsToURL(event.facets, event.value));
        });
        facetsFilterPanel.addEventListener("displayFacetFilter", function (event) {
            context.updatePage(applyDisplayFacetToURL(event.facet, event.value));
        });
        return facetsFilterPanel;
    }

    function initRangeFilterPanel(dateRangeFilterConfig) {
        var dateRangeFilterPanel = null;
        if (dateRangeFilterConfig.field && dateRangeFilterConfig.title) {
            dateRangeFilterPanel = new knowledgefinder.DateRangeFilterPanel("#range-filter", dateRangeFilterConfig);
            dateRangeFilterPanel.addEventListener("change", function (event) {
                context.updatePage(applyDateRangeFilterToURL(event.field, event.start, event.end));
            });
        }
        return dateRangeFilterPanel;
    }

    function initResultPanel(resultListConfig, exportConfig) {
        var exportTypes = null;
        if (exportConfig)
            exportTypes = Object.keys(exportConfig);

        var resultPanel = new knowledgefinder.ResultPanel("#resultPanel", pageData.fieldsDict, resultListConfig, exportTypes);
        resultPanel.addEventListener("moreInfo", function (id) {
            context.updatePage(applyDisplayDetailViewToURL(id));
        });
        resultPanel.addEventListener("mouseover", function (ids) {
            highlightFacetFilters(ids, true);
        });
        resultPanel.addEventListener("mouseout", function (ids) {
            highlightFacetFilters(ids, false);
        });
        resultPanel.addEventListener("goToPage", function (startPage) {
            var stateUrl = new StateUrl(pageData.url);
            stateUrl.setParameter(StateUrl.params.start, startPage);
            context.updatePage(stateUrl.data.url);
        });
        resultPanel.addEventListener("sortBy", function (value) {
            if (value) {
                var stateUrl = new StateUrl(pageData.url);
                stateUrl.setParameter(StateUrl.params.sort, value);
                context.updatePage(stateUrl.data.url);
            }
        });
        resultPanel.addEventListener("download", function (data) {
            var stateUrl = new StateUrl(pageData.url);
            var query = stateUrl.getParameter(StateUrl.params.query);
            var filterQuery = stateUrl.getParameter(StateUrl.params.filterQuery);

            connector.queryExportDocumentsInformation(query, filterQuery, pageData.fields.export[data.exportType], data.exportType, data.ids, function (exportString) {
                window.open("data:text/plain," + encodeURIComponent(exportString), "_blank");
            });
        });

        return resultPanel;
    }

    function initDetailView(detailViewConfig) {
        var detailView = new knowledgefinder.DetailView("knowledge-finder-2-modal", detailViewConfig);
        detailView.addEventListener("close", function () {
            context.updatePage(applyDisplayDetailViewToURL(null));
        });
        return detailView;
    }

    function initSelectionPanel() {
        var selectionPanel = new knowledgefinder.SelectionPanel("#current-selection");
        selectionPanel.addEventListener("removeFreeTextFilter", function (query) {
            context.updatePage(applyFreeTextFilterToURL(query, false));
        });
        return selectionPanel;
    }

    //------------------------------------------------------------------------------------------------------------------
    //  draw Panels
    //------------------------------------------------------------------------------------------------------------------
    context.updatePage = function (url, force) {
        // Takes the URL and executes and calls the necessary query functions based on the URL content.
        // The URL is only a way to save the current state but has no meaning for the server whatsoever
        console.log("updatePage");
        console.log(url);

        if (force)
            subElements.graphPanel.initGraph();

        if (!pageData.url)
            pageData.url = "";

        var stateUrl = new StateUrl(url);
        var parameters = stateUrl.getParameters();
        for (var param in StateUrl.params) {
            if (StateUrl.params.hasOwnProperty(param))
                console.log("load general (" + StateUrl.params[param] + "): ..." + parameters[StateUrl.params[param]]);
        }

        queue()
            .defer(function () {
                if (parameters[StateUrl.params.showId] && StateUrl.compareUrls(url, pageData.url, [StateUrl.params.showId]) === false)
                    showDetailView(parameters[StateUrl.params.showId], parameters[StateUrl.params.query], parameters[StateUrl.params.filterQuery]);
            })
            .defer(function () {
                if (StateUrl.compareUrls(url, pageData.url, [StateUrl.params.query]) === false)
                    drawSelectionPanel(parameters[StateUrl.params.query]);
            })
            .defer(function () {
                // the graph is an alternative visualisation for the facet filters
                var params = [
                    StateUrl.params.query,
                    StateUrl.params.filterQuery,
                    StateUrl.params.lowLevelGroups,
                    StateUrl.params.highLevelGroups
                ];
                if (force === true || StateUrl.compareUrls(url, pageData.url, params) === false) {
                    connector.queryFacetFilterInformation(parameters[StateUrl.params.query], parameters[StateUrl.params.filterQuery], function (nodes) {
                        drawFacetsFilterPanel(nodes, url);
                        drawGraphPanel(nodes, parameters[StateUrl.params.query], parameters[StateUrl.params.filterQuery], parameters[StateUrl.params.highLevelGroups], parameters[StateUrl.params.lowLevelGroups]);
                    })
                }
            })
            .defer(function () {
                var params = [
                    StateUrl.params.query,
                    StateUrl.params.filterQuery,
                    StateUrl.params.start,
                    StateUrl.params.rows,
                    StateUrl.params.sort
                ];
                if (StateUrl.compareUrls(url, pageData.url, params) === false)
                    drawResultListPanel(parameters[StateUrl.params.query], parameters[StateUrl.params.filterQuery], parameters[StateUrl.params.start], parameters[StateUrl.params.rows], parameters[StateUrl.params.sort]);
            })
            .defer(function () {
                var params = [
                    StateUrl.params.query,
                    StateUrl.params.filterQuery
                ];
                if (subElements.dateRangeFilterPanel && StateUrl.compareUrls(url, pageData.url, params) === false)
                    drawDateRangeFilterPanel(parameters[StateUrl.params.query], parameters[StateUrl.params.filterQuery], subElements.dateRangeFilterPanel);
            });

        pageData.url = url;
        // UPDATE URL, Only in FF and Chrome :) HTML5
        History.pushState("", "", pageData.url); //this doesn't actually call the url, it oly saves it in the history
    };

    function drawSelectionPanel(query) {
        query = cleanUpQuery(query);
        var stateUrl = new StateUrl("/").setParameter(StateUrl.params.query, query);
        var queries = stateUrl.getParameterValueList(StateUrl.params.query, StateUrl.delimiter.AND);
        subElements.selectionPanel.draw(queries);
    }

    function drawResultListPanel(query, filterQuery, start, rows, sort) {
        subElements.resultPanel.displayLoading(true);
        connector.queryDocumentList(query, filterQuery, pageData.fields.resultListEntry, start, rows, sort, pageData.fields.resultListEntryHighlight, function (docs, numberDocs, start, rows) {
            subElements.resultPanel.draw(docs, numberDocs, start, rows, sort);
            subElements.resultPanel.displayLoading(false);
        });
    }

    function drawFacetsFilterPanel(nodes, url) {
        subElements.facetsFilterPanel.displayLoading(true);
        subElements.facetsFilterPanel.draw(nodes, url);
        subElements.facetsFilterPanel.displayLoading(false);
    }

    function drawGraphPanel(nodes, query, filterQuery, highLevelGroups, lowLevelGroups) {
        subElements.graphPanel.displayLoading(true);
        if (!highLevelGroups)
            highLevelGroups = "";
        if (!lowLevelGroups)
            lowLevelGroups = "";

        var currentGroups = highLevelGroups.split(",");
        currentGroups = currentGroups.concat(lowLevelGroups.split(","));

        var displayedNodes = {};
        for (var index = 0; index < currentGroups.length; index++) {
            if (currentGroups[index] !== "")
                displayedNodes[currentGroups[index]] = nodes[currentGroups[index]];
        }

        var stateUrl = new StateUrl("/").setParameter(StateUrl.params.filterQuery, filterQuery);
        var filterQueries = stateUrl.getParameterValueList(StateUrl.params.filterQuery, StateUrl.delimiter.AND);

        var facetFiltersLookupTable = {};
        for (var key in pageData.facetsDataList)
            if (pageData.facetsDataList.hasOwnProperty(key))
                facetFiltersLookupTable[pageData.facetsDataList[key].query] = key;

        var selectedFilterValues = [];
        for (var i = 0; i < filterQueries.length; i++) {
            var infoQuery = facetFiltersLookupTable[filterQueries[i]];

            var info = pageData.facetsDataList[infoQuery];
            if (info !== undefined && info !== null) {
                selectedFilterValues.push(info);
            }
        }
        //var selectedFilterValues = utils.getSelectedFilterValues(filterQueries, data.facetsDataList);
        displayedNodes = subElements.graphPanel.updateNodes(displayedNodes, selectedFilterValues);

        // queue for neighbors
        var facets = [];
        for (index = 0; index < displayedNodes.length; index++) {
            var sourceNode = displayedNodes[index];
            var facet = pageData.facetsDataList[sourceNode.id];
            if (facet)
                facets.push(facet);
        }
        connector.queryFacetsConnections(facets, query, filterQuery, lowLevelGroups, highLevelGroups, function (response) {
            if (response) {
                subElements.graphPanel.updateEdges(response);
                subElements.graphPanel.draw();
                subElements.graphPanel.displayLoading(false);
            }
        });
    }

    function drawDateRangeFilterPanel(query, filterQuery, panel) {
        // todo don't use query for the range but create an extra url field
        var stateUrl = new StateUrl("/").setParameter(StateUrl.params.query, query);
        var queries = stateUrl.getParameterValueList(StateUrl.params.query, StateUrl.delimiter.AND);
        var currentRange = null;
        for (var index = 0; index < queries.length; index++) {
            if (queries[index].indexOf(panel.config.field + ":[") !== -1) {
                var rangeString = queries[index].replace(panel.config.field + ":[", "").replace("]", "");
                currentRange = rangeString.split(" TO ");
                currentRange = currentRange && currentRange.length === 2 ? currentRange : null;
                // don't consider selected range for querying the max range
                stateUrl.removeValueFromParameter(StateUrl.params.query, queries[index], StateUrl.delimiter.AND, StateUrl.delimiter.querySplit);
                query = stateUrl.getParameter(StateUrl.params.query);
                break;
            }
        }
        connector.queryDateRangeFilterRange(query, filterQuery, panel.config.field, function (minDoc, maxDoc) {
            panel.draw(minDoc, maxDoc, currentRange);
        });
    }

    function showDetailView(showId, query) {
        connector.queryDetailedDocumentInformation(showId, pageData.fields.detailView, pageData.fields.detailViewHighlight, function (docs) {
            if (docs.length > 0)
                subElements.detailView.open(cleanUpQuery(query), docs[0]);
        });
    }

    function cleanUpQuery(query) {
        var stateUrl = new StateUrl("/").setParameter(StateUrl.params.query, query);
        var queries = stateUrl.getParameterValueList(StateUrl.params.query, StateUrl.delimiter.AND);
        for (var index = 0; index < queries.length; index++)
            if (queries[index].indexOf(":[") !== -1)
                stateUrl.removeValueFromParameter(StateUrl.params.query, queries[index], StateUrl.delimiter.AND, StateUrl.delimiter.querySplit);
        return stateUrl.getParameter(StateUrl.params.query);
    }

    //------------------------------------------------------------------------------------------------------------------
    // apply changes to stateURL (+ return them)
    //------------------------------------------------------------------------------------------------------------------
    function applyDateRangeFilterToURL(field, start, end) {
        var stateUrl = new StateUrl(pageData.url);
        if (start && end) {
            //remove old range Query if there
            var queries = stateUrl.getParameterValueList(StateUrl.params.query, StateUrl.delimiter.AND);
            for (var index = 0; index < queries.length; index++)
                if (queries[index].indexOf(field + ":[") !== -1)
                    stateUrl.removeValueFromParameter(StateUrl.params.query, queries[index], StateUrl.delimiter.AND, StateUrl.delimiter.querySplit);
            //add new range query
            var query = field + ":[" + start + " TO " + end + "]";
            stateUrl.appendValueToParameter(StateUrl.params.query, query, StateUrl.delimiter.querySplit);
            stateUrl.setParameter(StateUrl.params.start, resultConfig.pages.initial); // config loaded via index.jsp
        }
        return stateUrl.data.url;
    }

    function applyFacetsFilterToURL(ids, value, facetsDataList) {
        var stateUrl = new StateUrl(pageData.url);
        ids.forEach(function (id) {
            var query = facetsDataList[id].query;
            if (value)
                stateUrl.appendValueToParameter(StateUrl.params.filterQuery, query, StateUrl.delimiter.querySplit);
            else
                stateUrl.removeValueFromParameter(StateUrl.params.filterQuery, query, StateUrl.delimiter.AND, StateUrl.delimiter.querySplit);
        });
        stateUrl.setParameter(StateUrl.params.start, resultConfig.pages.initial); // config loaded via index.jsp
        return stateUrl.data.url;
    }

    function applyFreeTextFilterToURL(query, value) {
        var stateUrl = new StateUrl(pageData.url);
        if (value)
            stateUrl.appendValueToParameter(StateUrl.params.query, query, StateUrl.delimiter.querySplit);
        else
            stateUrl.removeValueFromParameter(StateUrl.params.query, query, StateUrl.delimiter.AND, StateUrl.delimiter.querySplit);
        stateUrl.setParameter(StateUrl.params.start, resultConfig.pages.initial); // config loaded via index.jsp
        return stateUrl.data.url;
    }

    function applyDisplayDetailViewToURL(documentId) {
        if (!documentId)
            documentId = null; //close Detail View
        var stateUrl = new StateUrl(pageData.url);
        stateUrl.setParameter(StateUrl.params.showId, documentId);
        return stateUrl.data.url;
    }

    function applyDisplayFacetsToURL(facets, value) {
        var stateUrl = new StateUrl(pageData.url);
        for (var i = 0; i < facets.length; i++) {
            if (value)
                stateUrl.appendValueToParameter(StateUrl.params.highLevelGroups, facets[i], StateUrl.delimiter.COMMA);
            else {
                stateUrl.removeValueFromParameter(StateUrl.params.highLevelGroups, facets[i], StateUrl.delimiter.COMMA);
                stateUrl.removeValueFromParameter(StateUrl.params.lowLevelGroups, facets[i], StateUrl.delimiter.COMMA, StateUrl.delimiter.COMMA);
            }
        }
        return stateUrl.data.url;
    }

    function applyDisplayFacetToURL(facet, value) {
        var stateUrl = new StateUrl(pageData.url);
        if (value)
            stateUrl.appendValueToParameter(StateUrl.params.lowLevelGroups, facet, StateUrl.delimiter.COMMA);
        else
            stateUrl.removeValueFromParameter(StateUrl.params.lowLevelGroups, facet, StateUrl.delimiter.COMMA);
        return stateUrl.data.url;
    }

    //------------------------------------------------------------------------------------------------------------------
    //  highlighting
    //------------------------------------------------------------------------------------------------------------------
    function highlightFacetFilter(id, value) {
        subElements.facetsFilterPanel.setHighlight(id, value);
        subElements.resultPanel.setHighlight(id, value);
        subElements.graphPanel.highlightNode(id, value);
    }

    function highlightFacetFilters(ids, value) {
        subElements.graphPanel.highlightNodeList(ids, value);
        ids.forEach(function (id) {
            subElements.facetsFilterPanel.setHighlight(id, value);
        });
    }

    function highlightFacetsFilter(facetsFilterId, value) {
        if (facetsFilterId !== undefined) {
            subElements.graphPanel.highlightFacetsFilter(facetsFilterId, value);
        }
    }

    function highlightEdge(edge, value) {
        subElements.facetsFilterPanel.setHighlight(edge.source.id, value);
        subElements.resultPanel.setHighlight(edge.source.id, value);
        subElements.facetsFilterPanel.setHighlight(edge.target.id, value);
        subElements.resultPanel.setHighlight(edge.target.id, value);
        subElements.graphPanel.highlightEdge(edge, value);
    }

}(window.knowledgefinder.pageManager = window.knowledgefinder.pageManager || {}, window.knowledgefinder.connector, window.knowledgefinder.Url));