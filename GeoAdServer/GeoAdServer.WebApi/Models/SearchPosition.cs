using GeoAdServer.Domain.Entities.Events;
using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace GeoAdServer.WebApi.Models
{
    public class SearchPosition
    {
        [Required]
        public Coords P { get; set; }

        [Required]
        public double R { get; set; }
    }
}
