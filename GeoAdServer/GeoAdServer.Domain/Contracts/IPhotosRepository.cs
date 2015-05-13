using GeoAdServer.Domain.Entities;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace GeoAdServer.Domain.Contracts
{
    public interface IPhotosRepository
    {
        IEnumerable<Photo> GetByLocationId(int locationId);

        Photo GetById(int photoId);

        void Insert(Photo photo);

        void Update(Photo photo);

        bool Delete(int photoId);
    }
}
