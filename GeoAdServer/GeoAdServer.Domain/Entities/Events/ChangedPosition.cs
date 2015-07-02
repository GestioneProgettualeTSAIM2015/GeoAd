using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.ComponentModel.DataAnnotations;

namespace GeoAdServer.Domain.Entities.Events
{
    public class ChangedPosition : IEvent
    {
        [Required]
        public string Key { get; set; }

        [Required]
        public Coords NWCoord { get; set; }

        [Required]
        public Coords SECoord { get; set; }
    }

    public struct Coords {

        [Required]
        public string Lat { get; set; }

        [Required]
        public string Lng { get; set; }
    }
}
