using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace GeoAdServer.WebApi.Models
{
    public class LocationApiModel
    {
        public string PCat { get; set; }

        public string SCat { get; set; }

        [Required]
        public string Name { get; set; }

        public string Lat { get; set; }

        public string Lng { get; set; }

        public string Desc { get; set; }
    }
}
