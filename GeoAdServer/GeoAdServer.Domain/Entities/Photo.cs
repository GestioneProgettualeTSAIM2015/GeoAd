using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace GeoAdServer.Domain.Entities
{
    public class Photo
    {
        public int LocationId { get; set; }

        public string Base64Thumbnail { get; set; }

        public int Width { get; set; }

        public int Height { get; set; }
    }
}
