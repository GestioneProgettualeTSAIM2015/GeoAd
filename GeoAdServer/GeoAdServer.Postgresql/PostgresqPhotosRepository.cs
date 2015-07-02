using GeoAdServer.Domain.Contracts;
using GeoAdServer.Domain.Entities;
using GeoAdServer.Domain.Entities.DTOs;
using Npgsql;
using NpgsqlTypes;
using System;
using System.Collections.Generic;
using System.Data;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace GeoAdServer.Postgresql
{
    public class PostgresqPhotosRepository : AbstractPostgresqlRepository, IPhotosRepository
    {
        public PostgresqPhotosRepository(string connectionString) : base(connectionString)
        { }

        public PostgresqPhotosRepository(NpgsqlConnection connection) : base(connection)
        { }

        IEnumerable<PhotoDTO> IPhotosRepository.GetByLocationId(int locationId)
        {
            string query = @"SELECT ""Id"", ""Base64Thumbnail""
                             FROM public.""Photos""
                             WHERE ""LocationId"" = " + locationId;

            return ExecQuery<PhotoDTO>(query, (dr) =>
            {
                return new PhotoDTO
                {
                    Id = dr.Field<int>("Id"),
                    LocationId = locationId,
                    Base64Thumbnail = dr.Field<string>("Base64Thumbnail")
                };
            });
        }

        PhotoDTO IPhotosRepository.GetById(int photoId)
        {
            string query = @"SELECT ""LocationId"", ""Base64Thumbnail""
                             FROM public.""Photos""
                             WHERE ""Id"" = " + photoId;

            return ExecQuery<PhotoDTO>(query, (dr) =>
            {
                return new PhotoDTO
                {
                    Id = photoId,
                    LocationId = dr.Field<int>("LocationId"),
                    Base64Thumbnail = dr.Field<string>("Base64Thumbnail")
                };
            }).ElementAtOrDefault(0);
        }

        PhotoDataDTO IPhotosRepository.GetPhotoBase64Data(int photoId)
        {
            string query = @"SELECT ""Base64Data""
                             FROM public.""PhotosData""
                             WHERE ""PhotoId"" = " + photoId;

            return ExecQuery<PhotoDataDTO>(query, (dr) =>
            {
                return new PhotoDataDTO
                {
                    PhotoId = photoId,
                    Base64Data = dr.Field<string>("Base64Data")
                };
            }).ElementAtOrDefault(0);
        }

        int IPhotosRepository.Insert(Photo photo)
        {
            var templateCommand = @"INSERT INTO
                                   public.""Photos"" (""Id"", ""LocationId"", ""Width"", ""Height"", ""Base64Thumbnail"")
                                   VALUES (DEFAULT, {0}, {1}, {2}, '{3}')
                                   RETURNING ""Id""";

            object row = ExecCommand(string.Format(templateCommand,
                photo.LocationId, photo.Width, photo.Height, photo.Base64Thumbnail));

            return (int) row;
        }

        void IPhotosRepository.InsertData(int photoId, string base64Data)
        {
            var templateCommand = @"INSERT INTO
                                   public.""PhotosData"" (""PhotoId"", ""Base64Data"")
                                   VALUES ({0}, '{1}')";

            object row = ExecCommand(string.Format(templateCommand, photoId, base64Data));
        }

        bool IPhotosRepository.Delete(int photoId)
        {
            var templateCommand = @"DELETE FROM public.""PhotosData""
                                    WHERE ""PhotoId"" = {0}";

            ExecCommand(string.Format(templateCommand, photoId));

            templateCommand = @"DELETE FROM public.""Photos""
                                    WHERE ""Id"" = {0}";

            ExecCommand(string.Format(templateCommand, photoId));

            return true;
        }
    }
}
