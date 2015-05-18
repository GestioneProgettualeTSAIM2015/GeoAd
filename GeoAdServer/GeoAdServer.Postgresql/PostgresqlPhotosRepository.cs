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
    public class PostgresqlPhotosRepository : AbstractPostgresqlRepository, IPhotosRepository
    {
        public PostgresqlPhotosRepository(string connectionString) : base(connectionString)
        { }

        public PostgresqlPhotosRepository(NpgsqlConnection connection) : base(connection)
        { }

        IEnumerable<PhotoDTO> IPhotosRepository.GetByLocationId(int locationId)
        {
            string query = @"SELECT ""Id"", ""Data"", ""Width"", ""Height""
                             FROM public.""Photos""
                             WHERE ""LocationId"" = " + locationId;

            return ExecQuery<PhotoDTO>(query, (dr) =>
            {
                return new PhotoDTO
                {
                    Id = dr.Field<int>("Id"),
                    LocationId = locationId,
                    Width = dr.Field<int>("Width"),
                    Height = dr.Field<int>("Height")
                };
            });
        }

        PhotoDTO IPhotosRepository.GetById(int photoId)
        {
            string query = @"SELECT ""LocationId"", ""Width"", ""Height""
                             FROM public.""Photos""
                             WHERE ""Id"" = " + photoId;

            return ExecQuery<PhotoDTO>(query, (dr) =>
            {
                return new PhotoDTO
                {
                    Id = photoId,
                    LocationId = dr.Field<int>("LocationId"),
                    Width = dr.Field<int>("Width"),
                    Height = dr.Field<int>("Height")
                };
            }).ElementAtOrDefault(0);
        }

        byte[] IPhotosRepository.GetPhotoData(int photoId)
        {
            string query = @"SELECT ""Data""
                             FROM public.""Photos""
                             WHERE ""Id"" = " + photoId;

            return ExecQuery<byte[]>(query, (dr) =>
            {
                return dr.Field<byte[]>("Data");
            }).ElementAtOrDefault(0);
        }

        int IPhotosRepository.Insert(Photo photo)
        {
            var templateCommand = @"INSERT INTO
                                   public.""Photos"" (""Id"", ""LocationId"", ""Data"", ""Width"", ""Height"")
                                   VALUES (DEFAULT, {0}, :bytesData, {1}, {2})
                                   RETURNING ""Id""";

            NpgsqlParameter bytesData = new NpgsqlParameter(":bytesData", NpgsqlDbType.Bytea);
            bytesData.Value = photo.Data;

            object row = ExecCommand(string.Format(templateCommand,
                photo.LocationId, photo.Width, photo.Height), bytesData);

            return (int) row;
        }

        bool IPhotosRepository.Delete(int photoId)
        {
            var templateCommand = @"DELETE FROM public.""Photos""
                                    WHERE ""Id"" = {0}";

            object row = ExecCommand(string.Format(templateCommand, photoId));
            return true;
        }
    }
}
