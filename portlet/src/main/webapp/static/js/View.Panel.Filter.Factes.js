window.knowledgefinder = window.knowledgefinder || {};

knowledgefinder.FacetsFilterPanel = function (elementSelector, facetDict, fieldDict, config) {
    var self = this;
    self.data = {
        url: null,
        facetDict: facetDict,
        fieldDict: fieldDict,
        config: config
    };

    if (!elementSelector)
        elementSelector = "body";
    self.element = d3.select(elementSelector);

    // events (add, remove, displayFacetsFilter, displayFacetFilter, mouseover, mouseout, mouseoverFacetsFilter, mouseoutFacetsFilter)
    self.events = {};
};

knowledgefinder.FacetsFilterPanel.prototype.addEventListener = function (type, handler) {
    var self = this;
    console.log("register event:", type);
    if (self.events.hasOwnProperty(type))
        self.events[type].push(handler);
    else
        self.events[type] = [handler];
};

knowledgefinder.FacetsFilterPanel.prototype.triggerEvent = function (eventName, event) {
    var self = this;
    if (self.events.hasOwnProperty(eventName)) {
        console.log("trigger event:", eventName);
        self.events[eventName].forEach(function (handler) {
            handler(event);
        });
    }
};

knowledgefinder.FacetsFilterPanel.prototype.displayLoading = function (value) {
    var self = this;
    self.element.classed("loaded", !value);
};

//----------------------------------------------------------------------------------------------------------------------
// draw
//----------------------------------------------------------------------------------------------------------------------
knowledgefinder.FacetsFilterPanel.prototype.draw = function (nodes, url) {
    var self = this;
    // process data
    for (var group in nodes) {
        if (nodes.hasOwnProperty(group)) {
            var facetId = self.data.facetDict[self.data.fieldDict[group]].id;
            nodes[group].forEach(function (m) {
                m.id = facetId + "_" + m.id;
            });
        }
    }

    if (!document.getElementById("facets-filters").innerHTML)
        (function initTable() {
            console.log(self.data.config);

            if (self.data.config) {

                var drawCollapseIndicator = function (parent) {
                    var facetCollapseIndicator = parent.append("a")
                        .attr("class", "toggle-collapse-facet-filter collapsed")
                        .html(function (facetGroupItem) {
                            return facetGroupItem.children_filter || facetGroupItem.subItems ? "" : "&nbsp;"
                        })
                        .on("click", function (grp) {
                            d3.event.preventDefault();
                            var field = grp.subItemsFacet;
                            var show = false;
                            if (field) {
                                show = !isFacetSubFilterDisplayed(self.data.facetDict[grp.id].field);
                                self.triggerEvent("displayFacetFilter", {facet: field, value: show});
                            } else {
                                show = !isFacetsFilterDisplayed(grp);
                                field = [];
                                d3.select("#group-" + grp.id)
                                    .selectAll(".facet-level-0")
                                    .each(function (filter) {
                                        field.push(self.data.facetDict[filter.id].field);
                                    });
                                self.triggerEvent("displayFacetsFilter", {facets: field, value: show});
                            }
                        })
                        .filter(function (facetGroupItem) {
                            return facetGroupItem.children_filter || facetGroupItem.subItems;
                        });
                    facetCollapseIndicator.append("span")
                        .attr("class", "glyphicon glyphicon-plus");
                    facetCollapseIndicator.append("span")
                        .attr("class", "glyphicon glyphicon-minus");
                };

                var drawFacetFilter = function (parend, level, nodes) {
                    var facetList = parend.append("div")
                        .attr("id", function (facetGroup) {
                            return "table-" + facetGroup.id;
                        })
                        .attr("class", "panel-body panel-collapse collapse")
                        .append("div")
                        .attr("class", function (facetGroup) {
                            return facetGroup.scrollable ? "scroll" : "notscroll";
                        })
                        .append("ul")
                        .attr("class", "list-facet-level-" + level);

                    var facet = facetList.selectAll(".facet-level-" + level)
                        .data(function (facetGroup) {
                            if (nodes[facetGroup.subItemsFacet])
                                return nodes[facetGroup.subItemsFacet].filter(function (jsonEntry) {
                                    return jsonEntry.id.indexOf("_ANY") === -1;
                                });
                            if (facetGroup.subItems)
                                return facetGroup.subItems;
                            return [];
                        }, function (jsonEntry) {
                            return jsonEntry.id;
                        })
                        .enter()
                        .append("li")
                        .attr("id", function (facetGroupItem) {
                            var id = "filter-" + facetGroupItem.id;
                            return (facetGroupItem.count || facetGroupItem.count === 0) ? id : id + "__ANY";
                            //return "filter-" + facetGroupItem.id;
                        })
                        .attr("class", function (facetGroupItem) {
                            var classes = "facet-level-" + level + " filter";
                            if (facetGroupItem.cssClass)
                                classes = classes + " " + facetGroupItem.cssClass;
                            if (facetGroupItem.children_filter) // TODO + if actually has children
                                classes = classes + " filtergroup";
                            return classes;
                        })
                        .append("div")
                        .attr("class", "facet-filter panel panel-default");

                    (function drawFacetHeading(parend, level) {
                        var facetHeading = parend.append("div")
                            .attr("class", "panel-heading facet-level-" + level + "-header filter-spans")
                            .on("mouseenter", function (d) {
                                self.triggerEvent("mouseover", d.query ? d.id : d.id + "__ANY");
                            })
                            .on("mouseleave", function (d) {
                                self.triggerEvent("mouseout", d.query ? d.id : d.id + "__ANY");
                            });

                        drawCollapseIndicator(facetHeading.filter(function (facetGroupItem) {
                            return facetGroupItem.subItemsFacet;
                        }));

                        facetHeading.append("a")
                            .attr("class", "facet-label")
                            .attr("href", function (facetGroupItem) {
                                var link = "#filter-" + facetGroupItem.id;
                                return facetGroupItem.children_filter ? link + "__ANY" : link;
                                //return "#filter-" + facetGroupItem.id;
                            })
                            .on("click", function (d) {
                                d3.event.preventDefault();
                                if (getFilterDisabled(d) === false) {
                                    if (isFilterSelected(d) === false)
                                        self.triggerEvent("add", d.id);
                                    else
                                        self.triggerEvent("remove", d.id);
                                }
                            })
                            .append("span")
                            .attr("class", "name")
                            .text(function (facetGroupItem) {
                                return facetGroupItem.name;
                            });

                        var facetCount = facetHeading.append("div")
                            .attr("class", "count-remove");
                        facetCount.append("span")
                            .attr("class", "glyphicon glyphicon-remove")
                            .on("click", function (d) {
                                d3.event.preventDefault();
                                self.triggerEvent("remove", d.id);
                            });
                        facetCount.append("span")
                            .attr("class", "count badge");

                    })(facet, level);

                    return facet;
                };

                var facets = self.element.select("#facets-filters").selectAll(".facets-filter")
                    .data(self.data.config, function (entry) {
                        return entry.id;
                    })
                    .enter()
                    .append("div")
                    .attr("id", function (facetsGroup) {
                        return "group-" + facetsGroup.id;
                    })
                    .attr("class", function (facetsGroup) {
                        return "facets-filter panel panel-default " + facetsGroup.cssClass;
                    });

                var facetsTitle = facets.append("div")
                    .attr("class", "facets-header panel-heading")
                    .on("mouseenter", function (d) {
                        self.triggerEvent("mouseoverFacetsFilter", d.id);
                    })
                    .on("mouseleave", function (d) {
                        self.triggerEvent("mouseoutFacetsFilter", d.id);
                    })
                    .append("p")
                    .attr("class", "panel-title");

                drawCollapseIndicator(facetsTitle);

                facetsTitle.append("span")
                    .attr("class", "name")
                    .text(function (facetsGroup) {
                        return facetsGroup.name;
                    });

                var subGroup = drawFacetFilter(facets, 0, nodes);
                drawFacetFilter(subGroup, 1, nodes);
            }
        }());

    (function updateTable () {
        // update data
        var facetsFilters = self.element.selectAll(".facets-filter");
        var data = [];
        for (var subItemsFacet in nodes)
            if (nodes.hasOwnProperty(subItemsFacet))
                data = data.concat(nodes[subItemsFacet]);
        var filters = facetsFilters.selectAll(".filter");
        filters.data(data, function (jsonEntry) {
            return jsonEntry.id;
        });

        // collapse or open facets depending on url
        facetsFilters.each(function (facetsFilter) {
            var show = isFacetsFilterDisplayed(facetsFilter);
            d3.select(this).select(".panel-body").classed("in", show);
            d3.select(this).select(".toggle-collapse-facet-filter").classed("collapsed", !show);
        });
        filters.each(function (facetFilters) {
            if (self.data.facetDict[facetFilters.id]) {
                var show = isFacetSubFilterDisplayed(self.data.facetDict[facetFilters.id].field);
                d3.select(this).select(".panel-body").classed("in", show);
                d3.select(this).select(".toggle-collapse-facet-filter").classed("collapsed", !show);
            }
        });

        // update document count and mark facets with a count of 0 as disabled
        filters.select(".count")
            .text(function (facetGroupItem) {
                if (facetGroupItem.subItemsFacet) {
                    var facetGroupItemContent = nodes[facetGroupItem.subItemsFacet];
                    facetGroupItem.count = facetGroupItemContent[facetGroupItemContent.length - 1].count;
                }
                return facetGroupItem.count;
            });
        filters.select(".facet-label").classed("disabled", function (filter) { //selection
            if (filter.subItemsFacet) {
                var facetGroupItemContent = nodes[filter.subItemsFacet];
                filter = facetGroupItemContent[facetGroupItemContent.length - 1];
            }
            return getFilterDisabled(filter);
        });

        // sort
        var facetsLevel0 = facetsFilters.selectAll(".facet-level-0");
        facetsLevel0.sort(function (a, b) {
            return sortFilter(a, b);
        });

        facetsLevel0.selectAll(".facet-level-1").sort(function (a, b) {
            return sortFilter(a, b);
        });

        // display x instead of count if facet is selected
        filters.classed("selected", function (filter) {
            if (filter.subItemsFacet) {
                var facetGroupItemContent = nodes[filter.subItemsFacet];
                filter = facetGroupItemContent[facetGroupItemContent.length - 1];
            }
            return isFilterSelected(filter);
        });

        function sortFilter (filterA, filterB) {
            var selA = isFilterSelected(filterA);
            var selB = isFilterSelected(filterB);

            if (selA === true && selB === true) return 0;
            if (selA === true) return -1;
            if (selB === true) return 1;
            return filterB.count - filterA.count;
        }
    }());

    function isFacetsFilterDisplayed (facetsFilter) {
        var urlquery = new window.knowledgefinder.Url(url);
        if (facetsFilter.subItems.length === 0)
            return isFacetSubFilterDisplayed(facetsFilter.subItemsFacet);
        for (var index = 0; index < facetsFilter.subItems.length; index++) {
            var field = self.data.facetDict[facetsFilter.subItems[index].id].field;
            if (urlquery.parameterContainsValue(knowledgefinder.Url.params.highLevelGroups, field, knowledgefinder.Url.delimiter.COMMA))
                return true;
        }
        return false;
    }

    function isFacetSubFilterDisplayed (field) {
        var urlquery = new knowledgefinder.Url(url);
        return urlquery.parameterContainsValue(knowledgefinder.Url.params.lowLevelGroups, field, knowledgefinder.Url.delimiter.COMMA);
    }

    function isFilterSelected (filter) {
        var urlquery = new window.knowledgefinder.Url(url);
        return urlquery.parameterContainsValue(window.knowledgefinder.Url.params.filterQuery, filter.query, knowledgefinder.Url.delimiter.AND);
    }

    function getFilterDisabled (filter) {
        return filter.count <= 0;
    }
};

//----------------------------------------------------------------------------------------------------------------------
// highlighting
//----------------------------------------------------------------------------------------------------------------------
knowledgefinder.FacetsFilterPanel.prototype.setHighlight = function (id, value) { //value == true | false
    var self = this;
    self.element.select("#filter-" + id).classed("highlight", value);
};