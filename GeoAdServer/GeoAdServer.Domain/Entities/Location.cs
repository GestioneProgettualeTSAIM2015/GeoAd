using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace GeoAdServer.Domain.Entities
{
    public class Location
    {
        public int UserId { get; set; }

        public int PCatId { get; set; }

        public int? SCatId { get; set; }

        public string Name { get; set; }

        public string Lat { get; set; }

        public string Lng { get; set; }

        public string Desc { get; set; }

        public string Type { get; set; }
    }
}
