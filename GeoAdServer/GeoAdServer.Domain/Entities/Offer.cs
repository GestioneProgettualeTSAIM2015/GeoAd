using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace GeoAdServer.Domain.Entities
{
    public class Offer
    {
        [Required]
        public string Name { get; set; }

        [Required]
        public int LocationId { get; set; }

        [Required]
        public string Desc { get; set; }

        [Required]
        public long ExpDateMillis { get; set; }
    }
}
