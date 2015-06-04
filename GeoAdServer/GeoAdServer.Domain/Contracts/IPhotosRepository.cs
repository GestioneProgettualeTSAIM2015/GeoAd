using GeoAdServer.Domain.Entities;
using GeoAdServer.Domain.Entities.DTOs;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace GeoAdServer.Domain.Contracts
{
    public interface IPhotosRepository : IDisposable
    {
        IEnumerable<PhotoDTO> GetByLocationId(int locationId);

        PhotoDTO GetById(int photoId);

        PhotoDataDTO GetPhotoBase64Data(int photoId);

        int Insert(Photo photo);

        void InsertData(int photoId, string base64Data);

        bool Delete(int photoId);
    }
}
