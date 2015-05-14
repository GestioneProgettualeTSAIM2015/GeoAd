using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace GeoAdServer.Domain.Entities.DTOs
{
    public class LocationDTO
    {
        public int Id { get; set; }

        public string PCat { get; set; }

        public string SCat { get; set; }

        public string Name { get; set; }

        public string Lat { get; set; }

        public string Lng { get; set; }

        public string Desc { get; set; }

        public string Type { get; set; }
    }
}
