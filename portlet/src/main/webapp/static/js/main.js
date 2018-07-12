/**
 * Created by Anja Sonnenberg on 22.09.2015.
 *
 * See the file "LICENSE.txt" for the full license and copyright governing this code.
 *
 */

(function ($, context) {
    $(window)
        .ready(function () {
            context.pageManager.updatePage(context.pageManager.getCurrentStateUrl());
        })
        .load(function () {
            $(".scroll").mCustomScrollbar({
                "theme": "dark-thick",
                "autoHideScrollbar": false,
                scrollButtons: {
                    enable: true
                },
                advanced: {
                    updateOnContentResize: true
                }
            });
            $('#modalBody').css('max-height', $(window).height() - 180);
        })
        .on("resize", function () {
            $('#modalBody').css('max-height', $(window).height() - 180);
            context.pageManager.updatePage(context.pageManager.getCurrentStateUrl(), true);
        })
        .bind("popstate", function () {
            context.pageManager.updatePage(location.toString());
        });

})(jQuery, window.knowledgefinder = window.knowledgefinder || {});