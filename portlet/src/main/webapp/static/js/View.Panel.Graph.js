window.knowledgefinder = window.knowledgefinder || {};

knowledgefinder.GraphPanel = function (elementSelector, fieldsDict, facetsDataList, nodeLimit) {
    var self = this;
    self.settings = {
        height: null,
        width: null,
        margin: 15,

        maxNodeNum: nodeLimit,
        lowerLimitNodeValue: 0,
        nodeSizeRange: [5, 20],
        nodeTextSizeRange: [8, 20],

        lowerLimitEdgeValue: 0,
        edgeLengthRange: null,
        edgeWidthRange: [0.02, 5]
    };
    self.data = {
        fieldsDict: fieldsDict,
        facetsDataList: facetsDataList,
        simulation: null,
        graph: {
            nodes: [],
            links: [],
            maxNodeValue: null,
            minNodeValue: null,
            maxEdgeValue: null,
            minEdgeValue: null
        }
    };
    if (!elementSelector)
        elementSelector = "body";
    self.element = d3.select(elementSelector);
    self.subElements = {
        svg: null,
        textMessage: null,
        edges: null,
        nodes: null
    };

    // events (add, mouseoverNode, mouseoutNode, mouseoverEdge, mouseoutEdge)
    self.events = {};
};

knowledgefinder.GraphPanel.prototype.addEventListener = function (type, handler) {
    var self = this;
    console.log("register event:", type);
    if (self.events.hasOwnProperty(type))
        self.events[type].push(handler);
    else
        self.events[type] = [handler];
};

knowledgefinder.GraphPanel.prototype.triggerEvent = function (eventName, event) {
    var self = this;
    if (self.events.hasOwnProperty(eventName)) {
        console.log("trigger event:", eventName);
        self.events[eventName].forEach(function (handler) {
            handler(event);
        });
    }
};

knowledgefinder.GraphPanel.prototype.displayLoading = function (value) {
    var self = this;
    self.element.classed("loaded", !value);
};

knowledgefinder.GraphPanel.prototype.hide = function(value){
    if(value){
        //todo no hardcoding
        document.getElementById("graph-panel").querySelector("[data-parent='#graph-panel']").classList.add("collapsed");
        document.getElementById("graph1").classList.remove("in");
    }
};

//----------------------------------------------------------------------------------------------------------------------
// Graph
//----------------------------------------------------------------------------------------------------------------------
knowledgefinder.GraphPanel.prototype.initGraph = function () {
    console.log("init");
    var self = this;

    if (!self.settings.width)
        self.settings.width = parseInt(self.element.style("width"), 10);
    if (!self.settings.height)
        self.settings.height = parseInt(self.element.style("height"), 10);

    // clean svg with only a text message
    if (!self.subElements.svg) {
        self.subElements.svg = self.element.append("svg")
            .attr("width", self.settings.width)
            .attr("height", self.settings.height);
        self.subElements.textMessage = self.subElements.svg
            .append("g")
            .append("text")
            .attr("class", "graphinfo hide")
            .attr("x", self.settings.width / 2)
            .attr("y", self.settings.height / 2);
        self.subElements.textMessage.attr("text-anchor", "middle");
        // TODO define help-text and styles
        self.subElements.textMessage.text("No nodes, no graph :)");
    } else {
        self.subElements.edges.remove();
        self.subElements.nodes.remove();
    }

    self.subElements.edges = self.subElements.svg.selectAll('.link');
    self.subElements.nodes = self.subElements.svg.selectAll('.gnode');

    var maxEdgeLength = Math.min(self.settings.width, self.settings.height) / 2 - self.settings.margin * 2;
    self.settings.edgeLengthRange = [maxEdgeLength, maxEdgeLength / 1.2];

    self.data.simulation = d3.layout.force()
        .charge(-3800) // gravity between nodes
        .chargeDistance(self.settings.width * 2) // max charge distance, better performance
        .friction(0.3) // the particle velocity is scaled by the specified friction
        .gravity(1) // gravity to center
        .on("tick", function () {
            var placeInsideView = function (length, margin, value) {
                return Math.max(margin, Math.min(length - margin, value));
            };
            self.subElements.edges
                .attr("x1", function (d) {
                    return placeInsideView(self.settings.width, self.settings.margin, d.source.x);
                })
                .attr("y1", function (d) {
                    return placeInsideView(self.settings.height, self.settings.margin, d.source.y);
                })
                .attr("x2", function (d) {
                    return placeInsideView(self.settings.width, self.settings.margin, d.target.x);
                })
                .attr("y2", function (d) {
                    return placeInsideView(self.settings.height, self.settings.margin, d.target.y);
                });
            self.subElements.nodes
                .attr("transform", function (d) {
                    d.x = placeInsideView(self.settings.width, self.settings.margin, d.x);
                    d.y = placeInsideView(self.settings.height, self.settings.margin, d.y);
                    return "translate(" + d.x + "," + d.y + ")";
                });
        })
        .size([self.settings.width, self.settings.height]);

    return self;
};

knowledgefinder.GraphPanel.prototype.updateNodes = function (nodes, selectedFilterValues) {
    var self = this;
    var hashNodes = {};
    self.data.graph.nodes.map(function (node) {
        hashNodes[node.id] = node;
    });
    self.data.graph.nodes = [];
    self.data.graph.maxNodeValue = -Infinity;
    self.data.graph.minNodeValue = Infinity;

    var svgCenter = {
        x: self.settings.width * 0.5,
        y: self.settings.height * 0.5
    };

    var selectedFilterIds = selectedFilterValues.map(function (filter) {
        return filter.id;
    });


    for (var facetLevel0Name in nodes) {
        if (nodes.hasOwnProperty(facetLevel0Name)) {
            for (var nodeDataIndex = 0; nodeDataIndex < nodes[facetLevel0Name].length; nodeDataIndex++) {
                var nodeData = nodes[facetLevel0Name][nodeDataIndex];
                // ensure that the selected facets are not displayed as nodes
                if (selectedFilterIds.indexOf(nodeData.id) === -1 &&
                        // only display facets with documents as nodes
                    nodeData.count > self.settings.lowerLimitNodeValue &&
                        // and either the facet group title or its sub facets
                    (nodes[facetLevel0Name].length === 1 || nodeData.name !== "_ANY")) {
                    self.data.graph.maxNodeValue = Math.max(self.data.graph.maxNodeValue, parseInt(nodeData.count, 10));
                    self.data.graph.minNodeValue = Math.min(self.data.graph.minNodeValue, parseInt(nodeData.count, 10));

                    var node = hashNodes[nodeData.id];
                    if (node !== undefined) {
                        node.neighbors = [];
                        node.size = nodeData.count;
                    } else {
                        node = {
                            id: nodeData.id,
                            size: nodeData.count,
                            x: svgCenter.x,
                            y: svgCenter.y
                        };
                    }
                    self.data.graph.nodes.push(node);
                }
            }
        }
    }

    if(self.data.graph.nodes.length > self.settings.maxNodeNum) {
        self.data.graph.nodes.sort(function(node1, node2){
            return node2.size - node1.size;
        });
        self.data.graph.nodes.splice(self.settings.maxNodeNum);
    }

    return self.data.graph.nodes;
};

knowledgefinder.GraphPanel.prototype.updateEdges = function (jsonData) {
    var self = this;
    var hashNodes = {},
        hashEdges = {};
    self.data.graph.links = [];
    self.data.graph.maxEdgeValue = -Infinity;
    self.data.graph.minEdgeValue = Infinity;

    self.data.graph.nodes.map(function (node, index) {
        hashNodes[node.id] = index;
    });

    var generateNodeId = function (jsonNode, facetLevel0Name) {
        return self.data.fieldsDict[facetLevel0Name] + "_" + jsonNode.id;
    };

    var generateEdgeId = function (source, target) {
        var getId = function (value) {
            if (typeof value === "number" || typeof value === "string")
                return value;
            return value.id;
        };

        var sourceId = getId(source),
            targetId = getId(target);
        if (sourceId > targetId)
            return targetId + "-" + sourceId;
        else
            return sourceId + "-" + targetId;
    };

    for (var index = 0; index < jsonData.length; index++) {
        var sourceNode = self.data.graph.nodes[index];
        for (var facetLevel0Name in jsonData[index]) {
            if (jsonData[index].hasOwnProperty(facetLevel0Name)) {
                for (var dataIndex = 0; dataIndex < jsonData[index][facetLevel0Name].length; dataIndex++) {

                    var targetData = jsonData[index][facetLevel0Name][dataIndex];
                    var targetId = generateNodeId(targetData, facetLevel0Name);
                    var targetIndex = hashNodes[targetId];
                    if (targetData.count > self.settings.lowerLimitEdgeValue &&
                        targetIndex !== undefined && targetId != sourceNode.id) {

                        if (!sourceNode.neighbors)
                            sourceNode.neighbors = [targetId];
                        else if (sourceNode.neighbors.indexOf(targetId) === -1)
                            sourceNode.neighbors.push(targetId);

                        self.data.graph.maxEdgeValue = Math.max(self.data.graph.maxEdgeValue, parseInt(targetData.count, 10));
                        self.data.graph.minEdgeValue = Math.min(self.data.graph.minEdgeValue, parseInt(targetData.count, 10));

                        var edgeId = generateEdgeId(sourceNode.id, targetId);
                        var edgeIndex = hashEdges[edgeId];
                        if (edgeIndex === undefined) {
                            var newEdge = {
                                size: targetData.count,
                                target: self.data.graph.nodes[targetIndex],
                                source: sourceNode,
                                id: edgeId
                            };
                            self.data.graph.links.push(newEdge);
                            hashEdges[edgeId] = self.data.graph.links.indexOf(newEdge);
                        }
                    }
                }
            }
        }
    }
};

knowledgefinder.GraphPanel.prototype.draw = function () {
    console.log("draw");
    var self = this;
    drawNodes();
    drawLinks();
    self.data.simulation.nodes(self.data.graph.nodes);
    self.data.simulation.links(self.data.graph.links);
    self.data.simulation.start();
    self.subElements.textMessage.classed("hide", self.data.simulation.nodes().length > 0);

    function drawNodes(){
        console.log("draw Nodes");
        var scaleNodeSize = d3.scale.sqrt()
            .domain([self.data.graph.minNodeValue - 1, self.data.graph.maxNodeValue]).range(self.settings.nodeSizeRange);
        var scaleNodeTextSize = d3.scale.sqrt()
            .domain([self.data.graph.minNodeValue - 1, self.data.graph.maxNodeValue]).range(self.settings.nodeTextSizeRange);

        // update data
        self.subElements.nodes = self.subElements.nodes.data(self.data.graph.nodes, function (node) {
            return node.id;
        });

        // enter nodes for data with no element
        var newNode = self.subElements.nodes.enter()
            .append("g")
            .attr("class", function (node) {
                return self.data.facetsDataList[node.id].cssClass + " " + "gnode";
            })
            .attr("id", function (node) {
                return node.id;
            })
            .on("mousedown", function (node) {
                self.triggerEvent("add", [node.id]);
            })
            .on("mouseover", function (node) {
                //self._highlightNeighborhoods(node.id, true);
                self.triggerEvent("mouseoverNode", node.id);
            })
            .on("mouseout", function (node) {
                //self._highlightNeighborhoods(node.id, false);
                self.triggerEvent("mouseoutNode", node.id);

            })
            .call(self.data.simulation.drag);
        newNode.append("circle").attr("class", "node").attr("r", 0);
        newNode.append("text").attr("dx", ".10em").attr("dy", ".4em").style("font-size", "0px");

        self.subElements.nodes.selectAll("circle")
            .transition().duration(3000)
            .attr("r", function (node) {
                return scaleNodeSize(node.size);
            });
        self.subElements.nodes.selectAll("text")
            .transition().duration(3000)
            .style("font-size", function (node) {
                return scaleNodeTextSize(node.size) + "px";
            })
            .text(function (d) {
                return self.data.facetsDataList[d.id].name + " (" + d.size + ")";
            }
        );

        // remove nodes with no data binding
        self.subElements.nodes.exit().selectAll("circle")
            .transition().duration(3000).attr("r", 0)
            .remove();
        self.subElements.nodes.exit().remove();
    }
    return self;

    function drawLinks(){
        console.log("draw Links");
        var scaleEdgeLength = d3.scale.linear()
            .domain([self.data.graph.minEdgeValue - 1, self.data.graph.maxEdgeValue]).range(self.settings.edgeLengthRange);
        var scaleEdgeWidth = d3.scale.linear()
            .domain([self.data.graph.minEdgeValue - 1, self.data.graph.maxEdgeValue]).range(self.settings.edgeWidthRange);

        // update data
        self.data.simulation.linkDistance(function (edge) {
            return scaleEdgeLength(edge.size);
        });
        self.subElements.edges = self.subElements.edges.data(self.data.graph.links, function (edge) {
            return edge.id;
        });

        // enter edges for data with no element
        self.subElements.edges.enter().insert("line", ".gnode").attr("class", "link")
            .on("click", function (edge) {
                self.triggerEvent("add", [edge.target.id, edge.source.id]);
            })
            .on("mouseover", function (edge) {
                self.triggerEvent("mouseoverEdge", edge);
            })
            .on("mouseout", function (edge) {
                self.triggerEvent("mouseoutEdge", edge);
            });

        self.subElements.edges.transition().duration(3000)
            .style("stroke-width", function (edge) {
                return scaleEdgeWidth(edge.size);
            });

        // remove edges with no data binding
        self.subElements.edges.exit().remove();
    }
};

//----------------------------------------------------------------------------------------------------------------------
// highlighting
//----------------------------------------------------------------------------------------------------------------------
knowledgefinder.GraphPanel.prototype.highlightNode = function (id, value) {
    var self = this;
    if (value) {
        var html = "<b>" + self.data.facetsDataList[id].name + "</b> </br>";
        html += "Total documents with this property " + self.data.facetsDataList[id].size + "</br>";
        html += "Some info Extra to show here....";
        // hide
        self._showInfo(html);
    } else
        self._hidePanelInfo();
    self._highlightNeighborhoods(id, value);
};

knowledgefinder.GraphPanel.prototype.highlightEdge = function (edge, value) {
    var self = this;
    self._highlightNeighborhoods([edge.target.id, edge.source.id], value);
    if (value) {
        var source = self.data.facetsDataList[edge.source.id];
        var target = self.data.facetsDataList[edge.target.id];

        var html = "<b>" + source.name + " - " + target.name + "</b> </br>";
        html += "Current documents with both properties " + edge.size + "</br>";
        html += "Some info Extra to show here....";
        // hide
        self._showInfo(html);
    } else
        self._hidePanelInfo();
};

knowledgefinder.GraphPanel.prototype.highlightNodeList = function (ids, value) {
    var self = this;
    if (ids)
        self._fadeoutRemainingNodes(ids, value);
};

knowledgefinder.GraphPanel.prototype.highlightFacetsFilter = function (facetId, value) {
    var self = this;
    if (facetId) {
        var nodeIds = [];
        self.subElements.nodes
            .filter(function (node) {
                return self.data.facetsDataList[node.id].group === facetId;
            })
            .each(function (node) {
                nodeIds.push(node.id);
            });
        self._fadeoutRemainingNodes(nodeIds, value);
    }
};

knowledgefinder.GraphPanel.prototype._highlightNeighborhoods = function (ids, value) {
    var self = this;
    if (ids !== undefined) {
        if (!Array.isArray(ids))
            ids = [ids];
        var nodes = self.subElements.nodes.filter(function (node) {
            return ids.indexOf(node.id) !== -1;
        });
        nodes.classed("fadeup", value); //highlight node
        var unconnectedNodeIds = [];
        nodes.each(function (node) {
            unconnectedNodeIds = unconnectedNodeIds.concat([node.id]);
            unconnectedNodeIds = unconnectedNodeIds.concat(node.neighbors);
        });
        self._fadeoutRemainingNodes(unconnectedNodeIds, value);
    }
};

knowledgefinder.GraphPanel.prototype._fadeoutRemainingNodes = function (nodeIds, fadeout) {
    var self = this;
    self.subElements.nodes.classed("fadeout", function (node) {
        var inGroup = nodeIds.indexOf(node.id) !== -1;
        return (fadeout && !inGroup);
    });
    self.subElements.edges.classed("fadeout", function (edge) {
        var inGroup = nodeIds.indexOf(edge.source.id) !== -1 && nodeIds.indexOf(edge.target.id) !== -1;
        return (fadeout === true && !inGroup);
    });
};

knowledgefinder.GraphPanel.prototype._showInfo = function (html) {
    var divInfo = d3.select("#panelInfo");

    if (html !== null && html !== undefined) {
        divInfo.transition().duration(500).style("opacity", 1);
        divInfo.html(html);
    } else {
        divInfo.transition().duration(500).style("opacity", 1e-6);
    }
};

knowledgefinder.GraphPanel.prototype._hidePanelInfo = function () {
    var self = this;
    self._showInfo(null);
};