using GeoAdServer.Domain.Contracts;
using GeoAdServer.Domain.Entities;
using GeoAdServer.Domain.Entities.DTOs;
using Npgsql;
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
                    Data = dr.Field<byte[]>("Data"),
                    Width = dr.Field<int>("Width"),
                    Height = dr.Field<int>("Height")
                };
            });
        }

        PhotoDTO IPhotosRepository.GetById(int photoId)
        {
            string query = @"SELECT ""Id"", ""Data"", ""Width"", ""Height""
                             FROM public.""Photos""
                             WHERE ""Id"" = " + photoId;

            return ExecQuery<PhotoDTO>(query, (dr) =>
            {
                return new PhotoDTO
                {
                    Id = photoId,
                    LocationId = dr.Field<int>("LocationId"),
                    Data = dr.Field<byte[]>("Photo"),
                    Width = dr.Field<int>("Width"),
                    Height = dr.Field<int>("Height")
                };
            }).ElementAtOrDefault(0);
        }

        void IPhotosRepository.Insert(Photo photo)
        {
            var templateCommand = @"INSERT INTO
                                   public.""Photos"" (""Id"", ""LocationId"", ""Data"", ""Width"", ""Height"")
                                   VALUES (DEFAULT, {0}, {1}, {2}, {3})";

            object row = ExecCommand(string.Format(templateCommand,
                photo.LocationId, photo.Data, photo.Width, photo.Height));
        }

        void IPhotosRepository.Update(int id, Photo photo)
        {
            var templateCommand = @"UPDATE public.""Photos""
                                    SET ""LocationId"" = {0},
                                        ""Data"" = {1},
                                        ""Width"" = {2},
                                        ""Height"" = {3}
                                    WHERE ""Id"" = {4}";

            object row = ExecCommand(string.Format(templateCommand,
                photo.LocationId, photo.Data, photo.Width, photo.Height, id));
        }

        bool IPhotosRepository.Delete(int photoId)
        {
            var templateCommand = @"DELETE FROM public.""Photos""
                                    WHERE ""Id"" = {0}";

            object row = ExecCommand(string.Format(templateCommand, photoId));
            return ((int) row) == 1;
        }
    }
}
