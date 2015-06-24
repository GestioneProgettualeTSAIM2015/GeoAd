using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using GeoAdServer.Domain.Entities.DTOs;

namespace GeoAdServer.Domain.Entities.Events
{
    public class OfferingUpdated : IEvent
    {
        public OfferingDTO Offering { get; set; }

        public int LocationId { get; set; }

        public string LocationName { get; set; }

        public string Lat { get; set; }

        public string Lng { get; set; }
    }
}
