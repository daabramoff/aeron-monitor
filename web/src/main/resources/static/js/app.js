var app = new Vue({
  el: '#app',

  methods: {
    
    pollDriver() {
      const URL = '/api/v1//drivers/';

      var self = this;
      var poll =  function(){
        const Http = new XMLHttpRequest();
        Http.open('GET', URL + self.$data.selectedDriver.name );
        Http.onreadystatechange = function() {
          var d = self.$data.connectionState;
          var label = 'Disconnected';
          var color = 'red';
          if (this.status == 200) {
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
      const URL = '/api/v1//drivers';
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
    
    getConnections() {
      var self = this;
      var poll = function(url, data) {
        const Http = new XMLHttpRequest();
        Http.open('GET', url + self.$data.selectedDriver.name);
        Http.onreadystatechange = function() {
          data.length = 0;
          if (this.readyState == 4 && this.status == 200) {
            const resp = JSON.parse(Http.responseText); 
            resp.forEach(e => {
              data.push({
                id: e.id,
                streamId: e.streamId,
                channel: e.channel
              });
            });
          }
        };
        Http.send();
      };
      poll('/api/v1/cnc/publications/',  self.$data.publications.data);
      poll('/api/v1/cnc/subscriptions/', self.$data.subscriptions.data);
    },
    
    doPoll(idx) {
      var self = this;
      const pollFunctions = [this.getSystemCounters, this.getConnections];

      self.layoutPollIntervalIds.forEach(function(id) {
        clearInterval(id);
      });

      const t = self.$data.view.autoUpdateTimeout;
      if (t) {
        const f =  pollFunctions[idx];
        f(); 
        self.layoutPollIntervalIds[idx] = setInterval(function() {
          f();
        }.bind(this), 1000 * t);
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
      { icon: 'track_changes', text: 'System counters' },
      { icon: 'import_export', text: 'Connections' },
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
        { text: 'Id',         value: 'id',      align: 'right', sortable: true, width: 5 },
        { text: 'Name',       value: 'name',    align: 'left',  sortable: true  },
        { text: 'Value',      value: 'value',   align: 'right', sortable: false },
      ],
      data: []
    },
    
    publications: {
      headers: [
        { text: 'Id',         value: 'id',       align: 'right', sortable: true, width: 5 },
        { text: 'Channel',    value: 'channel',  align: 'left',  sortable: true },
        { text: 'Stream Id',  value: 'stream',   align: 'right', sortable: true },
      ],
      data: []
    },
    
    subscriptions: {
      headers: [
        { text: 'Id',         value: 'id',       align: 'right', sortable: true, width: 5 },
        { text: 'Channel',    value: 'channel',  align: 'left',  sortable: true },
        { text: 'Stream Id',  value: 'stream',   align: 'right', sortable: true },
      ],
      data: []
    },
    
  },
})