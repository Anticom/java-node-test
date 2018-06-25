//require('nashorn-polyfill/lib/timer-polyfill');
var TestCafe = require('testcafe');

var testCafe = new TestCafe (
    {
        controlPanelPort: 1337,
        servicePort: 1338,
        //testsDir : 'D:\\TestCafe-13.1\\tests',
        browsers: {
            'Mozilla Firefox': {
                //path : "C:\\Program Files (x86)\\Mozilla Firefox\\firefox.exe",
                path: "/usr/bin/firefox",
                cmd: "-new-window"
            }
        }
    });

var runOptions = {
    workers: testCafe.listConnectedWorkers(),
    browsers: testCafe.listAvailableBrowsers(),
    emulateCursor: true
};

testCafe.runTests(runOptions, function () {
    testCafe.on('taskComplete', function (report) {
        console.log(report);
    });
});