using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace GeoAdServer.WebApi.Models
{
    public class LocationPreferenceModel
    {
        [Required]
        public string Key { get; set; }

        [Required]
        public int Id { get; set; }
    }
}
