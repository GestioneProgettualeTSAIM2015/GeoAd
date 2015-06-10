using GeoAdServer.Domain.Entities.Events;

namespace GeoAdServer.Domain.Contracts
{
    public interface IEventsHandler
    {
        void Enqueue(IEvent command);

        IChangedPositionHandler ChpHandler { get; set; }
    }
}
