﻿using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace GeoAdServer.Domain.Entities.Events
{
    public class LocationDeleted : IEvent
    {
        public int LocationId { get; set; }
    }
}
