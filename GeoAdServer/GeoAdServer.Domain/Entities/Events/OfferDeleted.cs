using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace GeoAdServer.Domain.Entities.Events
{
    public class OfferDeleted : IEvent
    {
        public int OfferId { get; set; }

        public int LocationId { get; set; }

        public string Lat { get; set; }

        public string Lng { get; set; }
    }
}
