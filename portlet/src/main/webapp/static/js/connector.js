/**
 * Created by sonn_aj on 22.12.2017.
 */
(function (context, QueryUrl) {
    // query to get all facets -----------------------------------------------------------------------------------------
    // Configuration (allFields) loaded via index.jsp
    var facetsQuery = function(){
        var allGroupsName = Object.keys(allFields).join(",");
        return new QueryUrl(baseUrl + "get-nodes")
            .setParameter(QueryUrl.params.lowLevelGroups, allGroupsName)
            .setParameter(QueryUrl.params.highLevelGroups, allGroupsName)
            .setParameter(QueryUrl.params.limit, "-1")
            .setParameter(QueryUrl.params.query, "")
            .setParameter(QueryUrl.params.filterQuery, "");
    }();

    // -----------------------------------------------------------------------------------------------------------------
    // send methods (are called from the query methods)
    // -----------------------------------------------------------------------------------------------------------------
    var sendGetDocumentsRequest = function (url, callback) {
        queue().defer(d3.json, url).await(
            function (error, jsonR) {
                if (error) {
                    console.error(error);
                    throw error;
                }
                var docs = [];
                jsonR.docs.forEach(function (result) {
                    docs.push(formatDoc(result));
                });
                if (callback && typeof callback === "function")
                    callback(docs, jsonR.numFound, jsonR.start, jsonR.rows);
            });
    };

    //todo rewrite as sendSeveralGetDocumentsRequest
    var sendGetRangeRequest = function (urlMin, urlMax, callback) {
        queue().defer(d3.json, urlMin)
            .defer(d3.json, urlMax)
            .await(
            function (error, jsonMin, jsonMax) {
                if (error) {
                    console.error(error);
                    throw error;
                }
                if (callback && typeof callback === "function")
                    callback(formatDoc(jsonMin.docs[0]), formatDoc(jsonMax.docs[0]));
            });
    };

    //todo find better name
    var sendGetNodesRequest = function (url, callback) {
        queue().defer(d3.json, url).await(
            function (error, nodes) {
                if (error) {
                    console.error(error);
                    throw error;
                }
                if (callback && typeof callback === "function")
                    callback(nodes);
            });
    };

    var sendSeveralGetNodesRequest = function (urls, callback) {
        var nodesQueue = queue(1);
        for (var index = 0; index < urls.length; index++)
            nodesQueue.defer(d3.json, urls[index]);
        nodesQueue.awaitAll(function (error, jsonR) {
            if (error) {
                console.error(error);
                throw error;
            }
            if (callback && typeof callback === "function")
                callback(jsonR);
        });
    };

    var sendExportDocumentsRequest = function(url, callback){
        queue().defer(d3.json, url).await(
            function(error, json){
                if (error) {
                    console.error(error);
                    throw error;
                }
                if (json.docs.length > 0 && callback && typeof callback === "function") {
                    callback(json.exportString);
                }
            }
        );
    };

    //------------------------------------------------------------------------------------------------------------------
    // build queries for information and send them
    //------------------------------------------------------------------------------------------------------------------
    context.queryDateRangeFilterRange = function (query, filterQuery, field, callback) {
        console.log("queryDateRangeFilterRange");
        var queryUrl = new QueryUrl(baseUrl + "get-documents/")
            .setParameter(QueryUrl.params.query, query)
            .setParameter(QueryUrl.params.filterQuery, filterQuery)
            .setParameter(QueryUrl.params.fields, field)
            .setParameter(QueryUrl.params.start, "0")
            .setParameter(QueryUrl.params.rows, "1")
            .setParameter(QueryUrl.params.sort, field + " asc")
            .setParameter(QueryUrl.params.highlightFields, "");
        //filter all entries where field is empty
        queryUrl.appendValueToParameter(QueryUrl.params.query, field + ":[* TO *]", QueryUrl.delimiter.querySplit);
        var urlMinValue = queryUrl.data.url;

        queryUrl.setParameter(QueryUrl.params.sort, field + " desc");
        var urlMaxValue = queryUrl.data.url;

        sendGetRangeRequest(urlMinValue, urlMaxValue, callback);
    };

    context.queryDetailedDocumentInformation = function (id, fields, highlightFields, callback) {
        console.log("queryDetailedDocumentInformation");
        var queryUrl = new QueryUrl(baseUrl + "get-documents/")
            .setParameter(QueryUrl.params.query, "")
            .setParameter(QueryUrl.params.filterQuery, "id:" + id)
            .setParameter(QueryUrl.params.fields, fields)
            .setParameter(QueryUrl.params.start, 0)
            .setParameter(QueryUrl.params.rows, 1)
            .setParameter(QueryUrl.params.sort, "")
            .setParameter(QueryUrl.params.highlightFields, highlightFields);
        sendGetDocumentsRequest(queryUrl.data.url, callback);
    };

    context.queryFacetFilterInformation = function (query, filterQuery, callback) {
        console.log("queryFacetFilterInformation");
        var queryUrl = new QueryUrl(facetsQuery.data.url)
            .setParameter(QueryUrl.params.query, query)
            .setParameter(QueryUrl.params.filterQuery, filterQuery);
        sendGetNodesRequest(queryUrl.data.url, callback);
    };

    context.queryFacetsConnections = function (facets, query, filterQuery, lowLevelGroups, highLevelGroups, callback) {
        console.log("queryFacetsConnections");
        // queue for neighbors
        var urls = [];
        for (var index = 0; index < facets.length; index++) {
            var edgeUrl = new QueryUrl(facetsQuery.data.url)
                .setParameter(QueryUrl.params.query, query)
                .setParameter(QueryUrl.params.filterQuery, filterQuery)
                .setParameter(QueryUrl.params.lowLevelGroups, lowLevelGroups)
                .setParameter(QueryUrl.params.highLevelGroups, highLevelGroups);
            // Get sources
            edgeUrl.appendValueToParameter(QueryUrl.params.filterQuery, facets[index].query, QueryUrl.delimiter.querySplit);
            edgeUrl.setParameter(QueryUrl.params.limit, "-1");
            urls.push(edgeUrl.data.url);
        }
        sendSeveralGetNodesRequest(urls, callback);
    };

    context.queryDocumentList = function(query, filterQuery, fields, start, rows, sort, hightlightFields, callback){
        console.log("queryDocumentList");
        var queryUrl = new QueryUrl(baseUrl + "get-documents/")
            .setParameter(QueryUrl.params.query, query)
            .setParameter(QueryUrl.params.filterQuery, filterQuery)
            .setParameter(QueryUrl.params.fields, fields)
            .setParameter(QueryUrl.params.start, start)
            .setParameter(QueryUrl.params.rows, rows)
            .setParameter(QueryUrl.params.sort, sort)
            .setParameter(QueryUrl.params.highlightFields, hightlightFields);


        sendGetDocumentsRequest(queryUrl.data.url, function (docs, numberDocs, start, rows) {
            docs.forEach(function (result) {
                var allNodesUrlQuery = new QueryUrl(facetsQuery.data.url);
                allNodesUrlQuery.setParameter(QueryUrl.params.filterQuery, "id:" + result.id);
                allNodesUrlQuery.setParameter(QueryUrl.params.query, "");
                sendGetNodesRequest(allNodesUrlQuery.data.url, function (nodes) {
                    result.nodes = nodes;
                });
            });
            callback(docs, numberDocs, start, rows);
        });
    };

    context.queryExportDocumentsInformation = function(query, filterQuery, fields, exportType, ids, callback){
        console.log("queryExportDocumentsInformation");
        var queryUrl = new QueryUrl(baseUrl + "export-documents/")// baseUrl loaded via index.jsp
            .setParameter(QueryUrl.params.query, query)
            .setParameter(QueryUrl.params.fields, fields)
            .setParameter(QueryUrl.params.exportType, exportType.toLowerCase());

        if (!ids) {
            ids = [];
            queryUrl.setParameter(QueryUrl.params.filterQuery, filterQuery);
        }
        for (var idIndex = 0; idIndex < ids.length; idIndex++) {
            queryUrl.appendValueToParameter(QueryUrl.params.filterQuery, "id:" + ids[idIndex], QueryUrl.delimiter.queryOrSplit);
        }

        sendExportDocumentsRequest(queryUrl.data.url, callback);
    };

    var formatDoc = function (document) {
        var result = {};
        for (var index = 0; index < document.length; index++) {
            result[document[index].key] = document[index].value;
        }
        return result;
    }

}(window.knowledgefinder.connector = window.knowledgefinder.connector || {}, window.knowledgefinder.Url));