window.knowledgefinder = window.knowledgefinder || {};

knowledgefinder.DateRangeFilterPanel = function (elementSelector, config) {
    var self = this;
    self.config = {
        field: config.field,
        title: config.title,
        dateFormat: config.format
    };

    self.timeScale = null;
    self.brush = null;
    self.isoFormat = d3.time.format.utc("%Y-%m-%dT%H:%M:%S.%LZ");

    if (!elementSelector)
        elementSelector = "body";
    self.element = d3.select(elementSelector);
    self.element.append("div")
        .attr("class", "panel-heading")
        .append("div")
        .attr("class", "panel-title")
        .text(self.config.title);

    // events (change)
    self.events = {};

    // set up format date
    var formatString = "";
    if (self.config.dateFormat.day) {
        formatString += "%e";
    }
    if (self.config.dateFormat.month) {
        if (formatString !== "")
            formatString += " ";
        formatString += "%b";
    }
    if (self.config.dateFormat.year) {
        if (formatString !== "")
            formatString += " ";
        formatString += "%Y";
    }
    if (self.config.dateFormat.utc)
        self.formatDate = d3.time.format.utc(formatString);
    else
        self.formatDate = d3.time.format(formatString);
};

knowledgefinder.DateRangeFilterPanel.prototype.addEventListener = function (type, handler) {
    var self = this;
    console.log("register event:", type);
    if (self.events.hasOwnProperty(type))
        self.events[type].push(handler);
    else
        self.events[type] = [handler];
};

knowledgefinder.DateRangeFilterPanel.prototype.draw = function (minDoc, maxDoc, currentRange) {
    var self = this;
    var startDate = new Date(minDoc[self.config.field]);
    var endDate = new Date(maxDoc[self.config.field]);

    if (!self.timeScale || self.timeScale.domain()[0].getTime() !== startDate.getTime() || self.timeScale.domain()[1].getTime() !== endDate.getTime()) {

        var getCurrentHandlerValue = function (d) {
            var value = self.brush.extent();
            if (d === "w")
                return self.formatDate(value[0]);
            else {
                return self.formatDate(value[1]);
            }
        };

        var triggerEvent = function (eventName, event) {
            if (self.events.hasOwnProperty(eventName)) {
                console.log("trigger event:", eventName);
                self.events[eventName].forEach(function (handler) {
                    handler(event);
                });
            }
        };

        var drawSlider = function (width, height, marginLeft, marginTop) {
            self.timeScale.range([0, width - (2 * marginLeft)])
                .clamp(true);

            self.element.select("svg").remove();

            var svg = self.element.append("svg")
                .attr("viewBox", "0 0 " + width + " " + (height * 5))
                .attr("xmlns", "http://www.w3.org/2000/svg")
                .attr("class", "panel-body")
                .append("g")
                .attr("transform", "translate(" + marginLeft + ",0)");
            svg.append("g")
                .attr("class", "x axis")
                .attr("transform", "translate(0," + marginTop + ")")
                .call(d3.svg.axis()
                    .scale(self.timeScale)
                    .orient("bottom")
                    .tickSize(0)
                    .tickValues([]))
                .select(".domain")
                .attr("style", "stroke-width: " + height + "px;")
                .select(function () {
                    return this.parentNode.appendChild(this.cloneNode(true));
                })
                .attr("class", "halo")
                .attr("style", "stroke-width: " + (height - 2) + "px;");

            var slider = svg.append("g")
                .attr("class", "slider")
                .call(self.brush);
            slider.selectAll(".extent").remove();
            slider.select(".background")
                .attr("height", height)
                .attr("y", marginTop - 5);

            return slider;
        };

        var drawHandler = function (slider, height, marginTop) {
            var handler = slider.selectAll(".resize");
            handler.append("circle")
                .attr("class", "handle")
                .attr("transform", "translate(0," + marginTop + ")")
                .attr("r", height - 1);
            handler.append('text')
                .text(function (d) {
                    return getCurrentHandlerValue(d);
                })
                .attr("text-anchor", "middle")
                .attr("transform", "translate(0," + (height + 5) + ")");
            return handler;
        };

        self.timeScale = d3.time.scale().domain([startDate, endDate]);
        self.brush = d3.svg.brush().x(self.timeScale);
        if (currentRange) {
            var startRange = self.isoFormat.parse(currentRange[0]) > startDate ? self.isoFormat.parse(currentRange[0]) : startDate;
            var endRange = self.isoFormat.parse(currentRange[1]) < endDate ? self.isoFormat.parse(currentRange[1]) : endDate;
            self.brush.extent([startRange, endRange]);
        }
        else
            self.brush.extent([startDate, endDate]);

        //todo calculate the width
        var width = 300,
            height = 10,
            marginLeft = height * 2,
            marginTop = height * 3;
        var slider = drawSlider(width, height, marginLeft, marginTop);
        var handler = drawHandler(slider, height, marginTop);

        self.brush
            .on("brush", function () {
                handler.selectAll("text").text(function (d) {
                    return getCurrentHandlerValue(d);
                });
            })
            .on("brushend", function () {
                var value = self.brush.extent();
                if (value[0].getTime() === value[1].getTime()) {
                    value[1].setDate(value[1].getDate() + 1);
                    slider.call(self.brush.extent([value[0], value[1]]));
                    value = self.brush.extent();
                }

                var start = value[0], end = value[1];
                start = self.formatDate.parse(self.formatDate(start));
                end = self.formatDate.parse(self.formatDate(end));

                if (!self.config.dateFormat.month && !self.config.dateFormat.day && self.config.dateFormat.year)
                    end = d3.time.year.offset(end, 1);

                if (!self.config.dateFormat.day && self.config.dateFormat.month)
                    end = d3.time.month.offset(end, 1);

                if (self.config.dateFormat.day)
                    end = d3.time.day.offset(end, 1);

                triggerEvent("change", {
                    "field": self.config.field,
                    "start": self.isoFormat(start),
                    "end": self.isoFormat(end)
                });
            });

        var value = self.brush.extent();
        slider.call(self.brush.extent([value[0], value[1]]));
    }
    return self;
};