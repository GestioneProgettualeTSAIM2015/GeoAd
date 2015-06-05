using GeoAdServer.Domain.Entities.Events;

namespace GeoAdServer.Domain.Contracts
{
    public interface IEventsQueue
    {
        void Enqueue(IEvent command);
    }
}
