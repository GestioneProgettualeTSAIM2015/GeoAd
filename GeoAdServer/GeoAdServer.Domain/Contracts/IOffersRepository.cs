using GeoAdServer.Domain.Entities;
using GeoAdServer.Domain.Entities.DTOs;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace GeoAdServer.Domain.Contracts
{
    public interface IOffersRepository : IDisposable
    {
        IEnumerable<OfferDTO> GetAll(long? currentUTCMillis = null);

        IEnumerable<OfferDTO> GetByLocationId(int locationId, long? currentUTCMillis = null);

        OfferDTO GetById(int offerId);

        int Insert(Offer offer);

        bool Update(int id, Offer offer);

        bool DeleteById(int offerId);

        bool DeleteByLocationId(int locationId);
    }
}
