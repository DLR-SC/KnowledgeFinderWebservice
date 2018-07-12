window.knowledgefinder = window.knowledgefinder || {};

knowledgefinder.SelectionPanel = function (elementSelector) {
    var self = this;
    if (!elementSelector)
        elementSelector = "body";
    self.element = d3.select(elementSelector);

    // events (removeFreeTextFilter)
    self.events = {};
};

knowledgefinder.SelectionPanel.prototype.addEventListener = function (type, handler) {
    var self = this;
    console.log("register event:", type);
    if (self.events.hasOwnProperty(type))
        self.events[type].push(handler);
    else
        self.events[type] = [handler];
};

//----------------------------------------------------------------------------------------------------------------------
//  draw
//----------------------------------------------------------------------------------------------------------------------
knowledgefinder.SelectionPanel.prototype.draw = function (selectedFilterValues) {
    var self = this;

    // free text filter selections
    var freeTextButtons = self.element.selectAll(".text-selection")
        .data(selectedFilterValues, function (d) {
            return d;
        });
    var newFreeTextButtons = freeTextButtons.enter()
        .append("div")
        .attr("class", "text-selection selection btn btn-default")
        .on("click", function (x) {
            triggerEvent("removeFreeTextFilter", x);
        });
    newFreeTextButtons.append("span").attr("class", "name").text(function (x) {
        return x;
    });
    newFreeTextButtons.append("span").attr("class", "glyphicon glyphicon-remove");
    freeTextButtons.exit().remove();

    function triggerEvent (eventName, event) {
        if (self.events.hasOwnProperty(eventName)) {
            self.events[eventName].forEach(function (handler) {
                handler(event);
            });
        }
    }

    return self;
};