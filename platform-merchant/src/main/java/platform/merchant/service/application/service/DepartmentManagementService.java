package platform.merchant.service.application.service;


import platform.merchant.service.application.command.department.CreateDepartmentCommand;
import platform.merchant.service.application.command.department.CreateDepartmentResult;
import platform.merchant.service.application.command.department.DeleteDepartmentCommand;
import platform.merchant.service.application.command.department.DeleteDepartmentResult;
import platform.merchant.service.application.command.department.FetchDepartmentQuery;
import platform.merchant.service.application.command.department.FetchDepartmentResult;
import platform.merchant.service.application.command.department.GetDepartmentDetailQuery;
import platform.merchant.service.application.command.department.GetDepartmentDetailResult;
import platform.merchant.service.application.command.department.UpdateDepartmentCommand;
import platform.merchant.service.application.command.department.UpdateDepartmentResult;

public interface DepartmentManagementService {
    CreateDepartmentResult createDepartment(CreateDepartmentCommand command);

    UpdateDepartmentResult updateDepartment(UpdateDepartmentCommand command);

    DeleteDepartmentResult deleteDepartment(DeleteDepartmentCommand command);

    FetchDepartmentResult fetchDepartment(FetchDepartmentQuery query);

    GetDepartmentDetailResult getDepartmentDetail(GetDepartmentDetailQuery query);
}
