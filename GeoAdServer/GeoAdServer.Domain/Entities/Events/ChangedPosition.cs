using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace GeoAdServer.Domain.Entities.Events
{
    public class Position
    {
        public Coords NWCoord { get; set; }

        public Coords SECoord { get; set; }
    }

    public class ChangedPosition : IEvent
    {
        public string Key { get; set; }

        public Coords NWCoord { get; set; }

        public Coords SECoord { get; set; }
    }

    public struct Coords {

        public string Lat { get; set; }

        public string Lng { get; set; }
    }
}
