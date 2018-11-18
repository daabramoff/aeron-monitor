var app = new Vue({
  el: '#app',

  methods: {

    pollDriver() {
      const URL = '/api/v1/drivers/';

      var self = this;
      var poll =  function(){
        const Http = new XMLHttpRequest();
        Http.open('GET', URL + self.$data.selectedDriver.name );
        Http.onreadystatechange = function() {
          var d = self.$data.connectionState;
          var label = 'Disconnected';
          var color = 'red';
          if (this.readyState == 4 && this.status == 200) {
            const resp = JSON.parse(Http.responseText);
            if (!resp.active) {
              label = 'Stale';
              color = 'orange';
            } else {
              label = 'Active';
              color = 'green';
            }
          }
          if (d.label != label) {
            d.label = label;
            d.style.color = color;
          }
        };
        Http.send();
      };

      if (this.driverPollIntervalId) {
        clearInterval(this.driverPollIntervalId);
      }

      poll();
      self.driverPollIntervalId = setInterval(function() {
        poll();
      }.bind(self), 5000);
    },

    getDrivers() {
      const URL = '/api/v1/drivers';
      var self = this;
      const Http = new XMLHttpRequest();
      Http.open('GET', URL);
      Http.onreadystatechange = function() {
        if (this.readyState == 4 && this.status == 200) {
          const resp = JSON.parse(Http.responseText);
          self.$data.drivers = resp.slice();
          self.$data.selectedDriver.name = self.$data.drivers[0];
          self.doPoll(0);
        }
      };
      Http.send();
    },

    getSysInfo() {
      const URL = '/api/v1/sysInfo';
      var self = this;
      const Http = new XMLHttpRequest();
      Http.open('GET', URL);
      Http.onreadystatechange = function() {
        if (this.readyState == 4 && this.status == 200) {
          const resp = JSON.parse(Http.responseText);
          self.$data.sysInfo.data.forEach(e => { e.value = resp[e.key]; });
          app.$forceUpdate();
        }
      };
      Http.send();
    },

    getSystemCounters() {
      const URL = '/api/v1/cnc/systemCounters/';
      var self = this;
      const Http = new XMLHttpRequest();
      Http.open('GET', URL + self.$data.selectedDriver.name);
      Http.onreadystatechange = function() {
        var data = self.$data.counters.data;
        if (this.readyState == 4 && this.status == 200) {
          const resp = JSON.parse(Http.responseText);
          const counters = resp.counters;
          if (!counters) {
            return;
          }
          for (var i = 0; i != counters.length; i++) {
            const c = counters[i];
            var d = self.$data.counters.data[i];
            if (d) {
              d.id = i;
              d.name = c.label;
              d.value = c.value;
            } else {
              const n = {
                id: i,
                name: c.label,
                value: c.value
              };
              data.push(n);
            }
          }
          data.length = counters.length;
        } else {
          data.length = 0;
        }
      };
      Http.send();
    },

    getStreams() {
      const URL = '/api/v1/cnc/streams/';
      var self = this;
      var data = self.$data.streams.data;
      const Http = new XMLHttpRequest();
      Http.open('GET', URL + self.$data.selectedDriver.name);
      Http.onreadystatechange = function() {
        data.length = 0;
        if (this.readyState == 4 && this.status == 200) {
          const resp = JSON.parse(Http.responseText);
          resp.forEach(e => { data.push(e); });
        }
      };
      Http.send();
    },

    getLossRecords() {
      const URL = '/api/v1/cnc/lossRecords/';
      var self = this;
      var data = self.$data.lossRecords.data;
      const Http = new XMLHttpRequest();
      Http.open('GET', URL + self.$data.selectedDriver.name);
      Http.onreadystatechange = function() {
        data.length = 0;
        if (this.readyState == 4 && this.status == 200) {
          const resp = JSON.parse(Http.responseText);
          resp.forEach(e => { data.push(e); });
        }
      };
      Http.send();
    },

    getErrorRecords() {
      const URL = '/api/v1/cnc/errorRecords/';
      var self = this;
      var data = self.$data.errorRecords.data;
      const Http = new XMLHttpRequest();
      Http.open('GET', URL + self.$data.selectedDriver.name);
      Http.onreadystatechange = function() {
        data.length = 0;
        if (this.readyState == 4 && this.status == 200) {
          const resp = JSON.parse(Http.responseText);
          resp.forEach(e => { data.push(e); });
        }
      };
      Http.send();
    },

    doPoll(idx) {
      var self = this;
      const pollFunctions = [
        null,
        null,
        this.getSystemCounters,
        this.getStreams,
        this.getLossRecords,
        this.getErrorRecords,
      ];

      self.layoutPollIntervalIds.forEach(function(id) {
        clearInterval(id);
      });

      const t = self.$data.view.autoUpdateTimeout;
      if (t) {
        const f =  pollFunctions[idx];
        if (f) {
          f();
          self.layoutPollIntervalIds[idx] = setInterval(function() {
            f();
          }.bind(this), 1000 * t);
        }
      }
    },

    onDrawerItem(idx, e) {
      var d = this.$data.layoutVisible;
      d.fill(false);
      d[idx] = true;
      app.$forceUpdate();
      this.doPoll(idx);
    },
  },

  created: function () {
    this.getSysInfo();
    this.getDrivers();
  },

  watch: {
    'selectedDriver.name': function () {
      this.pollDriver();
    },

    'view.autoUpdateTimeout': function () {
      this.doPoll(this.layoutVisible.indexOf(true));
    }
  },

  data: {
    drawer: null,

    drawerItems: [
      { icon: 'info',          text: 'System' },
      { divider: true },
      { icon: 'track_changes', text: 'Counters' },
      { icon: 'import_export', text: 'Streams' },
      { icon: 'data_usage',    text: 'Loss records' },
      { icon: 'error_outline', text: 'Error records' },
    ],

    layoutVisible:         [true],
    layoutPollIntervalIds: [],

    view: {
      autoUpdateTimeout: 5,
      autoUpdateList: Array.from(Array(11).keys()),
    },

    connectionState: {
      label: 'Disconnected',
      style: {
        color: 'red',
        width: '140px',
        display: 'inline-block',
        'text-align': 'center'
      },
    },

    drivers: [],

    selectedDriver: {
      name: '',
      connected: false,
      active: false
    },

    counters: {
      headers: [
        { text: 'Id',      value: 'id',      align: 'left', sortable: true, width: 5 },
        { text: 'Name',    value: 'name',    align: 'left', sortable: true  },
        { text: 'Value',   value: 'value',   align: 'left', sortable: false },
      ],
      data: []
    },

    streams: {
      headers: [
        { text: 'Session', value: 'id',       align: 'left', sortable: true, width: 5 },
        { text: 'Channel', value: 'channel',  align: 'left', sortable: true },
        { text: 'Stream',  value: 'stream',   align: 'left', sortable: true },
      ],
      data: []
    },

    lossRecords: {
      headers: [
        { text: 'Session',           value: 'session', align: 'left',  sortable: true },
        { text: 'Channel',           value: 'channel', align: 'left',  sortable: true },
        { text: 'Stream',            value: 'stream',  align: 'left',  sortable: true },
        { text: 'Observation count', value: 'count',   align: 'left',  sortable: true },
        { text: 'Source',            value: 'source',  align: 'left',  sortable: true },
        { text: 'Total bytes lost',  value: 'total',   align: 'left',  sortable: true },
        { text: 'First observation', value: 'first',   align: 'left',  sortable: true },
        { text: 'Last observation',  value: 'last',    align: 'left',  sortable: true },
      ],
      data: []
    },

    errorRecords: {
      headers: [
        { text: 'Encoded exception', value: 'except',  align: 'left',  sortable: true },
        { text: 'First observation', value: 'first',   align: 'left',  sortable: true },
        { text: 'Last observation',  value: 'last',    align: 'left',  sortable: true },
        { text: 'Observation count', value: 'count',   align: 'left',  sortable: true },
      ],
      data: []
    },

    sysInfo: {
      headers: [
        { text: 'Label', value: 'key',    align: 'left',  sortable: false },
        { text: 'Value', value: 'value',  align: 'left',  sortable: false },
      ],
      data: [
        { label: 'Host name',       key: 'hostName'     },
        { label: 'PID',             key: 'jvmPid'       },
        { label: 'JVM name',        key: 'jvmName'      },
        { label: 'JVM version',     key: 'jvmVersion'   },
        { label: 'JVM vendor',      key: 'jvmVendor'    },
        { label: 'JVM start time',  key: 'jvmStartTime' },
      ]
    },
  },
})
