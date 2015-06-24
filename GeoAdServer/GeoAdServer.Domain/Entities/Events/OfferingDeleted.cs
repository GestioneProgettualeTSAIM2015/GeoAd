using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace GeoAdServer.Domain.Entities.Events
{
    public class OfferingDeleted : IEvent
    {
        public int OfferingId { get; set; }

        public int LocationId { get; set; }

        public string Lat { get; set; }

        public string Lng { get; set; }
    }
}
