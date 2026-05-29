package platform.merchant.service.application.service;


import platform.merchant.service.application.command.route.CreateRouteCommand;
import platform.merchant.service.application.command.route.CreateRouteResult;
import platform.merchant.service.application.command.route.DeleteRouteCommand;
import platform.merchant.service.application.command.route.DeleteRouteResult;
import platform.merchant.service.application.command.route.FetchDetailRouteQuery;
import platform.merchant.service.application.command.route.FetchDetailRouteResult;
import platform.merchant.service.application.command.route.FetchRoutesQuery;
import platform.merchant.service.application.command.route.FetchRoutesResult;
import platform.merchant.service.application.command.route.UpdateRouteCommand;
import platform.merchant.service.application.command.route.UpdateRouteResult;

public interface RouteManagementService {

    CreateRouteResult createRoute(CreateRouteCommand command);

    UpdateRouteResult updateRoute(UpdateRouteCommand command);

    DeleteRouteResult deleteRoute(DeleteRouteCommand command);

    FetchRoutesResult fetchRoutes(FetchRoutesQuery query);

    FetchDetailRouteResult fetchDetailRoute(FetchDetailRouteQuery build);
}
