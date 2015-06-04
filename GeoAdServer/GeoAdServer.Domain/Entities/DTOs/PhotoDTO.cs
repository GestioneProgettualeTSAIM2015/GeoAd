using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace GeoAdServer.Domain.Entities.DTOs
{
    public class PhotoDTO : ReflectiveEquals
    {
        public int Id { get; set; }

        public int LocationId { get; set; }

        public int Width { get; set; }

        public int Height { get; set; }

        public string Base64Thumbnail { get; set; }
    }
}
