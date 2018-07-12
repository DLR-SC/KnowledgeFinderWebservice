window.knowledgefinder = window.knowledgefinder || {};

knowledgefinder.Url = function (url) {
    var self = this;
    self.data = {
        url: url
    };
    return self;
};

knowledgefinder.Url.prototype.setParameter = function (param, value) {
    var self = this;
    var uri = URI(self.data.url);
    uri.setSearch(param, value);
    self.data.url = uri.toString();
    return self;
};

knowledgefinder.Url.prototype.getParameter = function (param) {
    var self = this;
    var uri = URI(self.data.url);
    return uri.search(true)[param];
};

knowledgefinder.Url.prototype.getParameters = function(){
    var self = this;
    var parameters = {};
    for ( var param in knowledgefinder.Url.params) {
        if(knowledgefinder.Url.params.hasOwnProperty(param))
            parameters[knowledgefinder.Url.params[param]] = self.getParameter(knowledgefinder.Url.params[param]);
    }

    return parameters;
};

knowledgefinder.Url.prototype.getParameterValueList = function (param, split) {
    var self = this;
    var valueList = [];
    var values = self.getParameter(param);
    if (values) {
        values.split(split).forEach(function (value) {
            value = value.trim();
            if (value)
                valueList.push(value);
        });
    }
    return valueList.sort();
};

knowledgefinder.Url.prototype.parameterContainsValue = function (param, value, split) {
    var self = this;
    var values = self.getParameterValueList(param, split.trim());
    if (!values || !value)
        return false;
    return values.indexOf(value.trim()) !== -1;
};

knowledgefinder.Url.prototype.appendValueToParameter = function (param, value, append) {
    var self = this;
    value = value.trim();

    if (self.parameterContainsValue(param, value, append) === false) {
        var values = self.getParameter(param);
        if (values)
            value = URI.decode(values) + append + value;
        self.setParameter(param, value);
    }
    return self;
};

knowledgefinder.Url.prototype.removeValueFromParameter = function (param, value, split, append) {
    var self = this;
    var values = self.getParameter(param);
    if (values) {
        var valueList = values.split(split);
        var remainingValues = [];
        for (var index = 0; index < valueList.length; index++) {
            var valueListEntry = valueList[index];
            if (knowledgefinder.Url.compareUrls(value, valueListEntry) === false)
                remainingValues.push(valueListEntry);
        }
        remainingValues = Array.prototype.map.call(remainingValues, function (entry) {
            return entry.trim();
        });
        values = remainingValues.join(append);
        self.setParameter(param, values);
    }
    return self;
};

knowledgefinder.Url.compareUrls = function (url1, url2, params) {
    if (!url1 || !url2)
        return url1 === url2;
    if (!params) // compare whole queries
        return url1.trim() == url2.trim(); // TODO: !== ???
    else { // compare components
        var queryUrl1 = new knowledgefinder.Url(url1),
            queryUrl2 = new knowledgefinder.Url(url2);
        for (var index = 0; index < params.length; index++)
            if (queryUrl1.getParameter(params[index]) != queryUrl2.getParameter(params[index])) // TODO: !== ???
                return false;
    }
    return true;
};

// ---------------------------------------------------------------------------------------------------------------------
// Enums
// ---------------------------------------------------------------------------------------------------------------------
knowledgefinder.Url.params = {
    query: "query",
    filterQuery: "filterQuery",
    lowLevelGroups: "groups",
    highLevelGroups: "groupsAnyValue",
    start: "start",
    rows: "rows",
    sort: "sort",
    showId: "show",
    limit: "limit",
    highlightFields: "highlightFields",
    fields: "fields",
    exportType: "exportType"
};
knowledgefinder.Url.params = Object.freeze(knowledgefinder.Url.params);

knowledgefinder.Url.delimiter = {
    AND: "AND",
    COMMA: ",",
    querySplit: " AND ",
    queryOrSplit: " OR "
};
knowledgefinder.Url.delimiter = Object.freeze(knowledgefinder.Url.delimiter);