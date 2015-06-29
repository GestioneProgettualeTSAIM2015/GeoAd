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
        [Required]
        public string PCat { get; set; }

        public string SCat { get; set; }

        [Required]
        public string Name { get; set; }

        [Required]
        public string Lat { get; set; }

        [Required]
        public string Lng { get; set; }

        [Required]
        public string Desc { get; set; }
    }
}
