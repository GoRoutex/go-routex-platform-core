package platform.merchant.service.application.service;


import platform.merchant.service.application.command.route.AssignRouteBatchCommand;
import platform.merchant.service.application.command.route.AssignRouteBatchResult;
import platform.merchant.service.application.command.route.AssignRouteCommand;
import platform.merchant.service.application.command.route.AssignRouteResult;
import platform.merchant.service.application.command.trip.CreateTripBatchCommand;
import platform.merchant.service.application.command.trip.CreateTripBatchResult;
import platform.merchant.service.application.command.trip.CreateTripCommand;
import platform.merchant.service.application.command.trip.CreateTripResult;
import platform.merchant.service.application.command.trip.DeleteTripCommand;
import platform.merchant.service.application.command.trip.DeleteTripResult;
import platform.merchant.service.application.command.trip.FetchTripDetailQuery;
import platform.merchant.service.application.command.trip.FetchTripDetailResult;
import platform.merchant.service.application.command.trip.FetchTripListQuery;
import platform.merchant.service.application.command.trip.FetchTripListResult;
import platform.merchant.service.application.command.trip.FetchScheduleOptimizationJobQuery;
import platform.merchant.service.application.command.trip.FetchScheduleOptimizationJobResult;
import platform.merchant.service.application.command.trip.ScheduleAsyncCommand;
import platform.merchant.service.application.command.trip.ScheduleAsyncResult;
import platform.merchant.service.application.command.trip.UpdateTripCommand;
import platform.merchant.service.application.command.trip.UpdateTripResult;

public interface MerchantTripService {

    CreateTripResult createTrip(CreateTripCommand command);

    CreateTripBatchResult createTripBatch(CreateTripBatchCommand command);

    UpdateTripResult updateTrip(UpdateTripCommand command);

    DeleteTripResult deleteTrip(DeleteTripCommand command);

    FetchTripDetailResult fetchDetail(FetchTripDetailQuery query);

    FetchTripListResult fetchTripList(FetchTripListQuery query);

    AssignRouteResult assignRoute(AssignRouteCommand command);

    AssignRouteBatchResult assignRouteBatch(AssignRouteBatchCommand command);

    ScheduleAsyncResult scheduleAsync(ScheduleAsyncCommand command);

    FetchScheduleOptimizationJobResult fetchScheduleOptimizationJob(FetchScheduleOptimizationJobQuery query);
}

