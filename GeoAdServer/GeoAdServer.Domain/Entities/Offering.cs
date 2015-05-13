using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace GeoAdServer.Domain.Entities
{
    public class Offering
    {
        public int Id { get; set; }

        public int LocationId { get; set; }

        public string Desc { get; set; }

        public DateTime InsDate { get; set; }

        public DateTime ExpDate { get; set; }
    }
}
