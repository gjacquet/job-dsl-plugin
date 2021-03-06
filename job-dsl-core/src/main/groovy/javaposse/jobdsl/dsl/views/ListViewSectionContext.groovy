package javaposse.jobdsl.dsl.views

import javaposse.jobdsl.dsl.AbstractContext
import javaposse.jobdsl.dsl.DslContext
import javaposse.jobdsl.dsl.JobManagement

import static javaposse.jobdsl.dsl.ContextHelper.executeInContext
import static javaposse.jobdsl.dsl.Preconditions.checkArgument

class ListViewSectionContext extends AbstractContext {
    private static final List<String> VALID_WIDTHS = ['FULL', 'HALF', 'THIRD', 'TWO_THIRDS']
    private static final List<String> VALID_ALIGNMENTS = ['CENTER', 'LEFT', 'RIGHT']

    String name
    String width = 'FULL'
    String alignment = 'CENTER'
    JobsContext jobsContext = new JobsContext()
    JobFiltersContext jobFiltersContext = new JobFiltersContext()
    ColumnsContext columnsContext = new ColumnsContext(jobManagement)

    ListViewSectionContext(JobManagement jobManagement) {
        super(jobManagement)
    }

    void name(String name) {
        this.name = name
    }

    void width(String width) {
        checkArgument(VALID_WIDTHS.contains(width), "width must be one of ${VALID_WIDTHS.join(', ')}")
        this.width = width
    }

    void alignment(String alignment) {
        checkArgument(VALID_ALIGNMENTS.contains(alignment), "alignment must be one of ${VALID_ALIGNMENTS.join(', ')}")
        this.alignment = alignment
    }

    void jobs(@DslContext(JobsContext) Closure jobsClosure) {
        executeInContext(jobsClosure, jobsContext)
    }

    /**
     * @since 1.29
     */
    void jobFilters(@DslContext(JobFiltersContext) Closure jobFiltersClosure) {
        executeInContext(jobFiltersClosure, jobFiltersContext)
    }

    void columns(@DslContext(ColumnsContext) Closure columnsClosure) {
        executeInContext(columnsClosure, columnsContext)
    }
}
