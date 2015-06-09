using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace GeoAdServer.Domain.Entities.DTOs
{
    public class CategoryDTO
    {
        public int Id { get; set; }

        public string Name { get; set; }

        public int? Aggregate { get; set; }
    }
}
