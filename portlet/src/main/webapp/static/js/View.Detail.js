window.knowledgefinder = window.knowledgefinder || {};

knowledgefinder.DetailView = function (elementId, configData) {
    var self = this;
    self.config = configData;
    self.element = document.getElementById(elementId);
    // events (close)
    self.events = {};

    $(self.element).on('hidden.bs.modal', function () {
        triggerEvent("close")
    });

    init();

    function triggerEvent(eventName, event) {
        if (self.events.hasOwnProperty(eventName)) {
            console.log("trigger event:", eventName);
            self.events[eventName].forEach(function (handler) {
                handler(event);
            });
        }
    }

    function init() {
        var container = d3.select(self.element).select("#modalBody");
        for (var groupIndex = 0; groupIndex < self.config.body.length; groupIndex++) {
            if (groupIndex)
                container.append("hr");

            var entries = container.selectAll(".modal-content-entry")
                .data(self.config.body[groupIndex], function (d) {
                    return d.field;
                });

            var resultEntry = entries.enter()
                .append("p")
                .attr("class", "modal-content-entry");
            resultEntry.append("strong")
                .attr("title", function (d) {
                    return d.tooltip;
                })
                .text(function (d) {
                    return d.title;
                });
            resultEntry.append("span");
        }
    }
};

knowledgefinder.DetailView.prototype.addEventListener = function (type, handler) {
    var self = this;
    console.log("register event:", type);
    if (self.events.hasOwnProperty(type))
        self.events[type].push(handler);
    else
        self.events[type] = [handler];
};

knowledgefinder.DetailView.prototype.open = function (query, doc) {
    var self = this;

    //------------------------------------------------------------------------------------------------------------------
    // draw
    //------------------------------------------------------------------------------------------------------------------
    // replace detail view title
    self.element.querySelector("#modalTitle").innerHTML = doc[self.config.title.field];

    var container = d3.select(self.element).select("#modalBody");

    var entries = container.selectAll(".modal-content-entry")
        .classed("hide", false);
    entries
        .select("span")
        .html(function (d) {
            if (doc[d.field]) {
                if (d.type === "date")
                    return ": " + new Date(doc[d.field]).toDateString();
                if (d.type === "link") {
                    var getLink = function (link) {
                        if (link.indexOf("http") !== 0 && link.indexOf("www") !== 0)
                            return link;
                        return "<a href='" + link + "' target='_blank'>" + link + "</a>";
                    };
                    var links = doc[d.field];
                    if (Array.isArray(links)) {
                        for (var linkIndex = 0; linkIndex < links.length; linkIndex++)
                            links[linkIndex] = getLink(links[linkIndex]);
                        return ": " + links.join(", ");
                    }
                    return ": " + getLink(links);
                }
                if (d.type === "localFile") {
                    var link = doc[d.field];
                    var url = (link.indexOf("http") !== 0 && link.indexOf("www") !== 0) ? "http://" + window.location.host + link : link;
                    return ": <a href='" + url + "' target='_blank'>download</a>";
                }
                if (Array.isArray(doc[d.field]))
                    return ": " + doc[d.field].join(", ");
                return ": " + doc[d.field];
            }
            return ": -";
        });

    entries
        .filter(function (d) {
            return (d.hideIfNotSet && !doc[d.field]);
        })
        .classed("hide", true);

    // this works only as log as the document content (original source)
    // remains the only searchable field not displayed in the modal!
    if (!self.element.querySelector("em") && query) { //todo test query
        container.insert("p", ":first-child")
            .append("em")
            .text("Note: Your search term was found in the original source!");
    } else {
        var firstChild = container.select("p");
        if (!firstChild.classed("modal-content-entry"))
            firstChild.remove();
    }
    //------------------------------------------------------------------------------------------------------------------
    // display
    //------------------------------------------------------------------------------------------------------------------
    $(self.element).modal("show");
    return self;
};