window.knowledgefinder = window.knowledgefinder || {};

knowledgefinder.ResultPanel = function (elementSelector, fieldDict, config, exportTypes) {
    var self = this;
    self.data = {
        docs: null,
        numberDocs: null,
        start: null,
        rows: null,
        selectedEntries: []
    };
    self.fieldDict = fieldDict;
    self.config = config;
    self.config.collapsed = true;

    if (!elementSelector)
        elementSelector = "body";
    self.element = d3.select(elementSelector);
    self.subElements = {
        collapse: {
            open: d3.select("#button-collapse-show"),
            close: d3.select("#button-collapse-hide")
        },
        exportOptions: d3.select("#export-options"),
        sortSelector: d3.select("#sortBy"),
        resultList: d3.select("#results"),
        resultEntries: null,
        selectInfo: d3.select("#select-info")
    };
    // events (mouseover, mouseout, moreInfo, goToPage, sortBy, download)
    self.events = {};

    self._initSortSelector(self.config.sortOptions);
    self._initCollapseButtons();
    self._initExportOptions(exportTypes);
};

knowledgefinder.ResultPanel.prototype.addEventListener = function (type, handler) {
    var self = this;
    console.log("register event:", type);
    if (self.events.hasOwnProperty(type))
        self.events[type].push(handler);
    else
        self.events[type] = [handler];

};

knowledgefinder.ResultPanel.prototype.triggerEvent = function (eventName, event) {
    var self = this;
    if (self.events.hasOwnProperty(eventName)) {
        self.events[eventName].forEach(function (handler) {
            handler(event);
        });
    }
};

//----------------------------------------------------------------------------------------------------------------------
//  draw
//----------------------------------------------------------------------------------------------------------------------
knowledgefinder.ResultPanel.prototype.draw = function (docs, numberDocs, start, rows, sortSelector) {
    var self = this;
    self.data.docs = docs;
    self.data.numberDocs = numberDocs;
    self.data.start = start;
    self.data.rows = rows;

    self._drawSortSelector(sortSelector);
    self._drawPagination();
    self._drawTable();

    return self;
};

knowledgefinder.ResultPanel.prototype.displayLoading = function (value) {
    var self = this;
    self.element.select(".load").classed("loaded", !value);
};

//----------------------------------------------------------------------------------------------------------------------
//  sort selector
//----------------------------------------------------------------------------------------------------------------------
knowledgefinder.ResultPanel.prototype._initSortSelector = function (sortOptions) {
    var self = this;

    self.subElements.sortSelector.selectAll("option").data(sortOptions)
        .enter()
        .append("option")
        .attr("value", function (d) {
            return d.value;
        })
        .text(function (d) {
            return d.text;
        });

    self.subElements.sortSelector.on("change", function () {
        d3.event.preventDefault();
        self.triggerEvent("sortBy", this.value);
    });
};

knowledgefinder.ResultPanel.prototype._drawSortSelector = function (sortOption) {
    var self = this;
    self.subElements.sortSelector.selectAll("option").each( //TODO easier without d3???
        function () {
            if (this.value == sortOption) {
                d3.select(this).attr("selected", "selected");
            } else {
                d3.select(this).attr("selected", null);
            }
        });
};

//----------------------------------------------------------------------------------------------------------------------
// export
//----------------------------------------------------------------------------------------------------------------------
knowledgefinder.ResultPanel.prototype._initExportOptions = function (exportTypes) {
    var self = this;
    if (exportTypes) {
        var exportOption = self.subElements.exportOptions.selectAll("li").data(exportTypes)
            .enter()
            .append("li");
        exportOption.append("a")
            .attr("href", "#")
            .text(function (d) {
                return d + " (all)";
            })
            .on("click", function (d) {
                d3.event.preventDefault();
                self.triggerEvent("download", {"exportType": d});
            });
        exportOption.append("a")
            .attr("href", "#")
            .text(function (d) {
                return d + " (selected)";
            })
            .on("click", function (d) {
                d3.event.preventDefault();
                self.triggerEvent("download", {"exportType": d, "ids": self.data.selectedEntries});
            });

        d3.select("#button-deselect-all").on("click", function () {
            d3.event.preventDefault();
            self.data.selectedEntries = [];
            self._drawTable();
        });
    } else if (!exportTypes || exportTypes.length === 0) {
        self.subElements.exportOptions.classed("hide", true);
        d3.select("#select").classed("hide", true);
        d3.select("#export-button").classed("hide", true);
    }
};

//----------------------------------------------------------------------------------------------------------------------
//  expand/collapse results buttons
//----------------------------------------------------------------------------------------------------------------------
knowledgefinder.ResultPanel.prototype._initCollapseButtons = function () {
    var self = this;
    self.setCollapsed(true);
    self.subElements.collapse.open.on("click", function () {
        self.setCollapsed(false);
    });
    self.subElements.collapse.close.on("click", function () {
        self.setCollapsed(true);
    });
};

knowledgefinder.ResultPanel.prototype.setCollapsed = function (value) {
    var self = this;

    self.subElements.resultList.selectAll(".panel-title a").classed("collapsed", value);
    self.subElements.resultList.selectAll(".collapse").classed("in", !value);
    self.config.collapsed = value;
    self.subElements.collapse.open.classed("hide", !value);
    self.subElements.collapse.close.classed("hide", value);
};

//----------------------------------------------------------------------------------------------------------------------
//  result list
//----------------------------------------------------------------------------------------------------------------------
knowledgefinder.ResultPanel.prototype._drawTable = function () {
    var self = this;

    // delete list and than redraw
    // d3 update pattern is not used, so the sorting order is not messed up
    if (self.subElements.resultEntries)
        self.subElements.resultEntries.remove();

    self.subElements.resultEntries = self.subElements.resultList.selectAll(".result").data(self.data.docs);

    var resultEntry = self.subElements.resultEntries.enter()
        .append("div")
        .attr("class", "result panel panel-default")
        .attr("id", function (d) {
            return "panel-" + d.id;
        })
        .on("mouseover", function (result) {
            self.triggerEvent("mouseover", self._extractIds(result.nodes));
        })
        .on("mouseout", function (result) {
            self.triggerEvent("mouseout", self._extractIds(result.nodes));
        });

    var resultHeading = resultEntry.append("div")
        .attr("class", "panel-heading");

    if (!self.subElements.exportOptions.classed("hide")) {
        resultHeading.append("div")
            .attr("class", "checkbox")
            .append("input")
            .attr("type", "checkbox")
            .on("change", function (result) {
                if (d3.event.target.checked) {
                    self.data.selectedEntries.push(result.id);
                } else {
                    var index = self.data.selectedEntries.indexOf(result.id);
                    if (index > -1) {
                        self.data.selectedEntries.splice(index, 1);
                    }
                }
                self.subElements.selectInfo.text(self.data.selectedEntries.length);
            })
            .filter(function (result) {
                var index = self.data.selectedEntries.indexOf(result.id);
                return index > -1;
            })
            .attr("checked", true);

        self.subElements.selectInfo.text(self.data.selectedEntries.length);
    }

    var resultTitle = resultHeading.append("p")
        .attr("class", "panel-title");

    var resultTitleCollapse = resultTitle.append("a")
        .attr("class", "collapsed")
        .attr("data-toggle", "collapse")
        .attr("href", function (d) {
            return "#" + d.id;
        });
    resultTitleCollapse.append("span").attr("class", "glyphicon glyphicon-minus");
    resultTitleCollapse.append("span").attr("class", "glyphicon glyphicon-plus");
    resultTitle.append("span")
        .html(function (d) {
            return d.title;
        });

    var resultBody = resultEntry.append("div")
        .attr("id", function (d) {
            return "" + d.id;
        })
        .attr("class", "panel-body panel-collapse collapse");

    for (var groupIndex = 0; groupIndex < self.config.body.length; groupIndex++) {
        if (groupIndex)
            resultBody.append("hr");
        for (var entryIndex = 0; entryIndex < self.config.body[groupIndex].length; entryIndex++) {
            var entry = resultBody.append("div")
                .attr("class", self.config.body[groupIndex][entryIndex].class);
            entry.append("h4")
                .text(self.config.body[groupIndex][entryIndex].title);
            entry.append("p")
                .html(function (result) {
                    var maxTextLength = 300;
                    if (!result[self.config.body[groupIndex][entryIndex].field])
                        return "";
                    var content = result[self.config.body[groupIndex][entryIndex].field];
                    if (Array.isArray(content))
                        content = content.join(", ");
                    if (content.length > maxTextLength) {
                        content = content.substring(0, maxTextLength - 3);
                        content += "...";
                    }
                    return content;
                });
        }
    }

    resultBody.append("p")
        .append("a")
        .attr("href", function (result) {
            return "#" + result.id;
        })
        .text("More Information")
        .on("click", function (result) {
            d3.event.preventDefault();
            return self.triggerEvent("moreInfo", result.id);
        });
};

//----------------------------------------------------------------------------------------------------------------------
//  pagination
//----------------------------------------------------------------------------------------------------------------------
knowledgefinder.ResultPanel.prototype._drawPagination = function () {
    var self = this;
    var urlquery = new knowledgefinder.Url(window.location.href);
    var itemsTotal = self.data.numberDocs;
    var currentStart = self.data.start;
    var itemsPage = self.data.rows;

    var numPages = Math.ceil(itemsTotal / itemsPage);
    var firstItemOfLastPage = ((numPages - 1) * itemsPage);

    var stats = d3.selectAll("#stats");
    var end = Math.min(currentStart + itemsPage, itemsTotal);
    var start = itemsTotal > 0 ? (currentStart + 1) : 0;
    stats.text("Showing " + start + "-" + end + " of " + itemsTotal + " entries.");

    var pagination = d3.selectAll(".pagination");
    var startPagination = Math.max(0, currentStart - (4 * itemsPage));
    var endPagination = Math.min(itemsTotal, currentStart + (5 * itemsPage));
    var pagesValues = d3.range(startPagination, endPagination, itemsPage);

    pagination.selectAll("li").remove();
    var pages = pagination.selectAll("li").data(pagesValues);

    if (currentStart > 0) {
        var previousPage = currentStart - itemsPage;
        pagination.append("li")
            .append("a")
            .attr("href", function () {
                urlquery.setParameter(knowledgefinder.Url.params.start, previousPage);
                return urlquery.data.url;
            })
            .on("click", function () {
                d3.event.preventDefault();
                self.triggerEvent("goToPage", previousPage);
            })
            .text("←");
    }

    if (startPagination > 0) {
        var firstPage = 0;
        pagination.append("li")
            .append("a")
            .attr("href", function () {
                urlquery.setParameter(knowledgefinder.Url.params.start, firstPage);
                return urlquery.data.url;
            })
            .on("click", function () {
                d3.event.preventDefault();
                self.triggerEvent("goToPage", firstPage);
            })
            .text("1");
    }

    if (startPagination > itemsPage) {
        var tenPagesPrevious = Math.max(currentStart - (itemsPage * 10), 0);
        pagination.append("li")
            .append("a")
            .attr("href", function () {
                urlquery.setParameter(knowledgefinder.Url.params.start, tenPagesPrevious);
                return urlquery.data.url;
            })
            .on("click", function () {
                d3.event.preventDefault();
                self.triggerEvent("goToPage", tenPagesPrevious);
            })
            .text("...");
    }

    pages.enter().append("li")
        .classed("active", function (x) {
            return currentStart == x;
        })
        .append("a")
        .attr("href", function (d) {
            urlquery.setParameter(knowledgefinder.Url.params.start, d);
            return urlquery.data.url;
        })
        .on("click", function (x) {
            d3.event.preventDefault();
            self.triggerEvent("goToPage", x);
        })
        .text(function (d) {
            return Math.floor(d / itemsPage) + 1;
        });

    if (endPagination <= firstItemOfLastPage - itemsPage) {
        var tenPagesNext = Math.min(currentStart + (itemsPage * 10), firstItemOfLastPage);
        pagination.append("li")
            .append("a")
            .attr("href", function () {
                urlquery.setParameter(knowledgefinder.Url.params.start, tenPagesNext);
                return urlquery.data.url;
            })
            .on("click", function () {
                d3.event.preventDefault();
                self.triggerEvent("goToPage", tenPagesNext);
            })
            .text("...");
    }

    if (endPagination <= firstItemOfLastPage) {
        var lastPage = firstItemOfLastPage;
        pagination.append("li")
            .append("a")
            .attr("href", function () {
                urlquery.setParameter(knowledgefinder.Url.params.start, lastPage);
                return urlquery.data.url;
            })
            .on("click", function () {
                d3.event.preventDefault();
                self.triggerEvent("goToPage", lastPage);
            })
            .text(numPages);
    }

    if (currentStart < firstItemOfLastPage) {
        var x2 = currentStart + itemsPage;
        pagination.append("li")
            .append("a")
            .attr("href", function () {
                urlquery.setParameter(knowledgefinder.Url.params.start, x2);
                return urlquery.data.url;
            })
            .on("click", function () {
                d3.event.preventDefault();
                self.triggerEvent("goToPage", x2);
            })
            .text("→");
    }
};

//----------------------------------------------------------------------------------------------------------------------
//  highlight
//----------------------------------------------------------------------------------------------------------------------
knowledgefinder.ResultPanel.prototype.setHighlight = function (idFilter, value) { //value == true | false
    var self = this;
    self.subElements.resultEntries.filter(
        function (result) {
            if (!result.nodes)
                return false;
            var nodesId = self._extractIds(result.nodes);
            return nodesId.indexOf(idFilter) !== -1;
        })
        .classed("highlight", value);
};

knowledgefinder.ResultPanel.prototype._extractIds = function (nodes) {
    var self = this;
    var ids = [];
    for (var groupField in nodes) {
        if (nodes.hasOwnProperty(groupField)) {
            var subGroupNodes = nodes[groupField];
            subGroupNodes.forEach(function (n) {
                if (n.count > 0) {
                    var id = self.fieldDict[groupField] + "_" + n.id;
                    ids.push(id);
                }
            });
        }
    }
    return ids;
};

//todo better handel private methods