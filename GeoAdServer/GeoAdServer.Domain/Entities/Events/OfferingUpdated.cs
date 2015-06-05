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
    }
}
