/**
 * Created by mwag on 26.02.15.
 *
 * See the file "LICENSE.txt" for the full license and copyright governing this code.
 *
 */

var fs = require('fs');
var path = require('path');
var assert = require('assert');

console.log('   079   args: ' + process.argv);
reportDirectory = process.argv[process.argv.length -1];
//reportDirectory = path.dirname(process.argv[process.argv.length -2]);
//reportDirectory = config.dir.test.structure.reports.path;

var headerBuffer = '<?xml version="1.0" encoding="UTF-8"?>\n<?xml-stylesheet type="text/xsl" href="xunit_refine.xsl"?>\n';

var newTestsuiteName = process.argv[process.argv.length - 1];
var oldTestsuiteName;

var reportBuffer='';

var fileList = fs.readdirSync(path.join(reportDirectory, 'raw'));
console.log('   082   ' + fileList);
var file;
var reportBuffer = '';
for (ii=0; ii<fileList.length; ii++) {
    file = path.join(reportDirectory, 'raw', fileList[ii]);

    //var tmpReportBuffer = fs.readFileSync(reportDirectory + '/raw/Firefox_38.0.0_(Ubuntu_0.0.0).xml').toString();
    var tmpReportBuffer = '';
    tmpReportBuffer = fs.readFileSync(file).toString();

    var startIndex = tmpReportBuffer.indexOf('<?xml version=\"');
    var endIndex = tmpReportBuffer.indexOf('>', startIndex);
    tmpReportBuffer = tmpReportBuffer.substring(endIndex+1);

    ///////
    // In case the testsuite is not run because of an error: add error for header/statistic
    ///////
    // Correct counts in report header
    var startSuiteIndex = tmpReportBuffer.search("<testsuite name=\"");
    var endSuiteIndex = tmpReportBuffer.search('>', startIndex);
    var origTestSuiteDescription = '';
    var newTestSuiteDescription = '';
    origTestSuiteDescription = tmpReportBuffer.substring(startSuiteIndex, endSuiteIndex);
    //console.log('   090   ' + origTestSuiteDescription);

    var tmpStartIndex = 0;
    //var tmpEndIndex = 0;

    var testCount = 0;
    tmpStartIndex = origTestSuiteDescription.search('tests="');
    if (tmpStartIndex < 0) {
        //tmpEndIndex = origTestSuiteDescription.search('"', tmpStartIndex);
        //console.log('   091   ' + origTestSuiteDescription.substring(tmpStartIndex, tmpEndIndex));
        newTestSuiteDescription = origTestSuiteDescription.concat(' tests="1" errors="1"');
        //console.log('   093   ' + newTestSuiteDescription);
        tmpReportBuffer = tmpReportBuffer.replace(origTestSuiteDescription, newTestSuiteDescription);
    }
    /////// end of error correction

    // Remove properties tag
    var endTag = '</properties>';
    startIndex = tmpReportBuffer.indexOf('<properties>');
    endIndex = tmpReportBuffer.indexOf('</properties>', startIndex);
    toBeReplacedString = tmpReportBuffer.substring(startIndex, endIndex + '</properties>'.length + 1);
    //console.log('   081   ' + toBeReplacedString + startIndex + ' ' + endIndex + endTag.length);
    reportBuffer = reportBuffer.concat(tmpReportBuffer.replace(toBeReplacedString, ''));

}
var filename = path.join(reportDirectory, 'test_report.xml');

// Opens the fd for the new file
filedescriptor = fs.openSync(filename, 'w');

// Add header
fs.writeFileSync(filename, headerBuffer);
fs.appendFileSync(filename, "\n<testsuites>");

// Add reports
fs.appendFileSync(filename, reportBuffer);

// Add closing tag
fs.appendFileSync(filename, "</testsuites>\n");
fs.closeSync(filedescriptor);
