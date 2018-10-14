var app = new Vue({
  el: '#app',

  methods: {
    onDrawerItem() {
      console.log("================");
      console.log(arguments);
      console.log("================");
    },
    
    getDrivers() {
      var self = this;
      const Http = new XMLHttpRequest();
      Http.open('GET', '/api/v1/drivers');
      Http.onreadystatechange = function() {
        if (this.readyState == 4 && this.status == 200 ) {
          const resp = JSON.parse(Http.responseText); 
          self.$data.drivers = resp.slice();
          self.$data.selectedDriver.name = self.$data.drivers[0];
        }
      };
      Http.send();
    },
    
    pollDriver() {
      var self = this;
      var poll =  function(){
        const Http = new XMLHttpRequest();
        Http.open('GET', '/api/v1//drivers/' + self.$data.selectedDriver.name );
        Http.onreadystatechange = function() {
          if (this.readyState == 4 && this.status == 200 ) {
            var t = 1000;
            const resp = JSON.parse(Http.responseText); 
            if (!resp) {
              self.$data.connectionState.color = 'red';
              self.$data.connectionState.label = 'Disconnected';
            } else {
              if (!resp.active) {
                self.$data.connectionState.color = 'orange';
                self.$data.connectionState.label = 'Connected';
              } else {
                self.$data.connectionState.color = 'green';
                self.$data.connectionState.label = 'Active';
                t = 10000;
              }
            }

            self.driverPollIntervalId = setTimeout(function() {
              poll();
            }.bind(self), t);
          }
        };
        Http.send();
      };
      
      if (this.driverPollIntervalId) {
        clearTimeout(this.driverPollIntervalId);
      }
      poll();
    },

    pollCounters() {
      var self = this;
      var poll =  function(){
        const Http = new XMLHttpRequest();
        Http.open('GET', '/api/v1/cnc/systemCounters/' + self.$data.selectedDriver.name );
        Http.onreadystatechange = function() {
          if (this.readyState == 4 && this.status == 200 ) {
            const resp = JSON.parse(Http.responseText); 
            const counters = resp.counters; 
            var data = self.$data.counters.data;
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
          }
        };
        Http.send();
      };
      
      if (this.countersPollIntervalId) {
        clearInterval(this.countersPollIntervalId);
      }

      const t = this.$data.counters.autoUpdateTimeout;
      if (t) {
        poll();
        this.countersPollIntervalId = setInterval(function() {
          poll();
        }.bind(this), 1000 * t);
      }
    }
  },
  
  created: function () {
    this.getDrivers();
    this.pollCounters();
  },
  
  watch: {
    'selectedDriver.name': function () {
      this.pollDriver();
    },
    
    'counters.autoUpdateTimeout': function () {
      this.pollCounters();
    }
  },
  
  data: () => ({
    drawer: null,

    drawerItems: [
      { icon: 'track_changes', text: 'System counters' },
      { icon: 'import_export', text: 'Connections' },
      { divider: true },
      { icon: 'error_outline', text: 'Errors' },
    ],
    
    drivers: [],

    selectedDriver: {
      name: '',
      connected: false,
      active: false
    }, 
    
    connectionState: {
      color: 'red',
      label: 'Disconnected'
    },
    
    counters: {
      autoUpdateTimeout: 5,
      autoUpdateList: Array.from(Array(11).keys()),
      headers: [
        { text: 'Id', value: 'id',    align: 'right', sortable: true, width: 5 },
        { text: 'Name',    value: 'name',  align: 'left',  sortable: true },
        { text: 'Value',   value: 'value', align: 'right', sortable: false },
      ],
      data: []
    }
  }),
})